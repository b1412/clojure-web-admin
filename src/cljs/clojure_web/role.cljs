(ns clojure-web.role
  (:require-macros [cljs.core.async.macros :refer [go]]
                   [secretary.core         :refer [defroute]]
                   [taoensso.tower  :as tower-macros])
  (:require  [reagent.core :as reagent]
             [re-frame.core :refer [dispatch subscribe]]
             [clojure-web.user :refer [user-panel]]
             [clojure-web.subs ]
             [clojure-web.handlers]
             [clojure-web.components.reforms :refer [form]]
             [clojure-web.components.entity-bs-table :refer [create-bs-table ]]
             [clojure-web.components.react-bootstrap :refer [Button ButtonToolbar
                                                             Glyphicon Table
                                                             Tabs Tab]]
             [taoensso.tower :as tower ]
             [cljs-http.client :as http]
             [inflections.core :refer  [plural titleize]]
             [clojure-web.components.common :refer [show-on-click show-when select-to-show
                                                    call-method alert dialog]]))


(defn assign-table [& {:keys [data show?]}]
  (dispatch [:resources {:limit 10000}])
  (let [resources (subscribe [:resources])
        state (reagent/atom true)]
    (show-when
     (reagent/create-class
      {:component-did-mount
       (fn []
         (->> (:resources @data)
              (map (fn [e]
                     (let [chk (js/$ (str ":checkbox[value=" (:resource-id e) "]"))
                           a   (js/$ (str "#" (:resource-id e)))]
                       (.prop chk "checked" true)
                       (.text (js/$ a) (:scope e)))))
              (doall))
         (.editable (js/$ "#resources a")
                    (clj->js {
                              :type "select"
                              :title "Select scope"
                              :placement "right"
                              :value "system"
                              :source [{:value "system" :text "system"}
                                       {:value "orgs" :text "orgs"}
                                       {:value "org" :text "org"}
                                       {:value "user" :text "user"}]})))
       :reagent-render
       (fn []
         (let [state (reagent/atom false)]
           [:div
            [Tabs
             (doall
              (for [[title v] (group-by :entity @resources)]
                ^{:key title}
                [Tab {:event-key title :title title}
                 [Table
                  {:id "resources"
                   :striped true
                   :bordered true
                   :condensed true
                   :hover true}
                  [:thead
                   [:tr
                    [:th]
                    (->>  ["Key" "Uri" "Desc" "Scope"]
                          (map-indexed (fn [idx itm] ^{:key idx} [:th itm])))]]
                  [:tbody
                   (doall
                    (for [item v]
                      ^{:key (:id item)}
                      [:tr
                       [:td [:input {:type "checkbox" :value (:id item)}]]
                       [:td (:key item)]
                       [:td (:uri item)]
                       [:td (:desc item)]
                       [:td [:a {:id (:id item) :href "#"}  "system"]]]))]]]))]
           ]))})
     resources)))


(def tconfig
  {:fallback-locale :en
   :compiled-dictionary (tower-macros/dict-compile*
  {:en   {:create "Create"
          :delete "Delete"
          :edit "Edit"
          :metadata "Metadata"
          :new "New"
          :charts "Charts"
          :import "Import"
          :export "Export"}
   :zh   {:create "创建"
          :delete "删除"
          :edit "编辑"
          :metadata "元数据"
          :new "新增"
          :charts "报表"
          :import "导入"
          :export "导出"}})})

(def t (tower/make-t tconfig))




(defn assign-resources []
  (let [table-data (atom {})
        submit-fn (fn [data]
              (go
                (let [url (str "/roles/" (:id data) "/resources")
                      params (dissoc data :resources)
                      res (<! (http/put  url {:form-params params}))]
                  (if (:success res)
                    (dialog :message "Updated successfully")
                    (dialog :message (:body res) :type (aget js/BootstrapDialog "TYPE_DANGER"))))))]
    (fn []
      [select-to-show
       :trigger [Button {:bs-style "info"}
                 [Glyphicon {:glyph "user"}] "Assign"]
       :event :role-ress
       :select-fn (fn []
                    (->>
                     (call-method "role" "getSelections")
                     (first)
                     (js->clj)
                     (map (fn [[k v]] [(keyword k) v]))
                     (into {})))
       :dispatch-fn (fn [row]
                      (dispatch [:get-role-ress (:id @row)]))
       :load-data-fn (fn [body row data]
                       (reset! table-data
                               {:id (:id @row)
                                :resources @data})
                       body)
       :selected-on-hide #(dispatch [:reset-role-ress])
       :body [assign-table :data table-data]
       :footer
       [[Button {:bs-style "primary"
                 :on-click (fn []
                             (let [checked (js/$ "input:checked")]
                               (goog.array.forEach
                                checked (fn [val index arr]
                                          (let [a (.find (.parent (.parent (js/$ val))) "a")
                                                resource-id (.attr (js/$ a) "id")
                                                scope (.html (js/$ a))]
                                            (when resource-id
                                              (swap! table-data assoc (keyword resource-id) scope)))))
                               (dispatch [:reset-role-ress])
                               (submit-fn @table-data)
                               (reset! table-data {})))}
         "Save"]
        [Button {} "Cancel"]]])))










(def role-panel (create-bs-table
                 :entity "role"
                 :btns {"get-role-resources"
                        [assign-resources]}))
