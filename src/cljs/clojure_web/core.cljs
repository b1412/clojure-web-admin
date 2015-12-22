(ns clojure-web.core
  (:require-macros [cljs.core.async.macros :refer [go]]
                   [secretary.core         :refer [defroute]]
                   [reagent.ratom :refer [reaction]])
  (:require [goog.events                   :as    events]
            [reagent.core                  :as    reagent]
            [re-frame.core :refer [dispatch subscribe]]
            [secretary.core                :as    secretary]
            [re-com.core                   :refer [h-box v-box box gap
                                                   line scroller border label
                                                   title alert-box p button
                                                   horizontal-tabs] ]
            [re-com.util                   :refer [get-element-by-id ]]

            [clojure-web.subs]
            [clojure-web.handlers]
            [clojure-web.role :refer [role-panel]]
            [clojure-web.user :refer [user-panel]]
            [clojure-web.brand :refer [brand-panel]]
            [clojure-web.computer :refer [computer-panel]]
            [clojure-web.brand :refer [brand-panel]]
            [clojure-web.resource :refer [resource-panel]]
            [clojure-web.organization :refer [organization-panel]]
            [clojure-web.components.react-bootstrap
             :refer
             [Tabs Tab TreeView Button Modal ModalBody ModalFooter ModalHeader
              ModalTitle Glyphicon]]
            [clojure-web.components.common :refer [show-when show-on-click]]
            [goog.history.EventType        :as    EventType]
            [shodan.console :as console :include-macros true]
            [clojure.string :as str])
  (:import [goog History]))


(def panels {"brands" brand-panel
             "computers" computer-panel
             "users" user-panel
             "roles" role-panel
             "resources" resource-panel
             "organizations" organization-panel})


(defn get-children-components [root coll]
  (filter (every-pred #(= (:type %) "menu") #(= (:parent_id %) (:id root))) coll))



(defn menu-tree [root coll]
  (let [children (get-children-components root coll)]
    {:text (:label root)
     :id (:key root)
     :selectable false
     :expanded false
     :nodes (->> children
                 (mapv (fn [child]
                         (if (seq (get-children-components child coll))
                           (merge {:selectable false
                                   :expanded false}
                                  (menu-tree  child coll))
                           (hash-map
                            :id (:key child)
                            :selectable true
                            :text (:label child))))))}))



(defn get-menu-tree [permission]
  (let [root {:id 0 :label "Menus"}]
    (menu-tree root permission)))

(defn get-menu-by-id [node-id menu-tree]
  (first (filter #(= node-id (:key %)) menu-tree)))

(defroute menu-route "/:tab" [tab]
  (let [menu-tree (subscribe [:permissions])
        new-tab (get-menu-by-id tab @menu-tree)]

    (dispatch [:selected-tab-id tab])
    (when new-tab
      (prn new-tab)
      (dispatch [:add-tab new-tab]))))

(def history (History.))

(events/listen history
               EventType/NAVIGATE
               (fn [event]
                 (secretary/dispatch! (.-token event))))

(.setEnabled history true)

(defn on-select-tab [tab]
  (.setToken history (menu-route {:tab tab})))



(defn right-side-tabs
  [tabs selected-tab-id ]
  (fn[tabs selected-tab-id]
    [v-box
     :gap   "10px"
     :children [[Tabs {:active-key selected-tab-id :on-select (fn [key] (on-select-tab key))}
                 (for [tab tabs]
                   ^{:key (:key tab)}
                   [Tab {:event-key (:key tab) :title (:label tab)}
                    [(panels (:key tab))]])]]]))

(defn title-box
  []
  [h-box
   :justify :center
   :align   :center
   :height  "62px"
   :style   {:background-color "#337ab7"}
   :children [[title
               :label "Clojure Web Admin"
               :level :level1
               :style {:font-size   "20px"
                       :width       "200px"
                       :color       "#fefefe"}]]])





(defn left-side-menu-tree
  [permission selected-tab-id]
  (let [menu-tree (get-menu-tree permission)
        data {:color "#428bca",
              :expandIcon "glyphicon glyphicon-folder-close",
              :collapseIcon "glyphicon glyphicon-folder-open",
              :showTags true
              :showBorder false
              :onNodeSelected (fn [event node]
                                (on-select-tab ((js->clj node) "id")))
              :data  [menu-tree]}

        new-tab (get-menu-by-id selected-tab-id permission)]
    (when new-tab
      (prn new-tab)
      (dispatch [:add-tab new-tab]))
    (reagent/create-class
     {:component-did-mount
      (fn []
        (.treeview (js/$ "#tree") (clj->js data)))
      :reagent-render
      (fn []
        [:div#com
         [:div#tree]])})))

(defn main
  []
  (let [permission (subscribe [:permissions])
        tabs (subscribe [:tabs])
        selected-tab-id (subscribe [:selected-tab-id])
        show? (reagent/atom false)]
    (fn []
      [v-box
       :children
       [[:nav.navbar.navbar-default
         [:div.container-fluid
          [:div.navbar-header]
          [:p.navbar-text.navbar-right
           [:a.navbar-link { :href "/logout" } [Glyphicon {:glyph "log-out"}] "Logout"]]]]
        [Modal
         {:show @show?
          :on-hide (fn [e] (reset! show? false))
          :bs-size "large"
          :aria-labelledby "contained-modal-title-lg"}
         [ModalHeader [ModalTitle "Logout"] ]
         [ModalBody [Glyphicon {:glyph "question-sign"}] "Are you sure you want to log-off?"]
         [ModalFooter
          [Button {:on-click (fn [e] (reset! show? false))} "Ok" ]]]
        [h-box
         :children [[show-when
                     [scroller
                      :width "20%"
                      :size  "200px"
                      :v-scroll :auto
                      :h-scroll :on
                      :child [v-box
                              :children [[title-box]
                                         [left-side-menu-tree
                                          @permission
                                          @selected-tab-id]]]]
                     permission]
                    [show-when
                     [scroller
                      :width "80%"
                      :child [v-box
                              :size  "auto"
                              :children [[right-side-tabs @tabs @selected-tab-id]]]]
                     tabs]]]]])))



(defn mount-components []
  (dispatch [:get-permissions])
  (reagent/render [main]
                  (get-element-by-id "app")))

(defn ^:export init!
  []
  (mount-components))
