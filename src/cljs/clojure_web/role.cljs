(ns clojure-web.role
  (:require-macros [cljs.core.async.macros :refer [go]]
                   [secretary.core         :refer [defroute]])
  (:require  [reagent.core                  :as    reagent]
             [re-frame.core :refer [dispatch subscribe]]
             [clojure-web.subs ]
             [clojure-web.handlers]
             [clojure-web.components.bs-table :refer [create-bs-table]]
             [clojure-web.components.react-bootstrap :refer [Button ButtonToolbar
                                                             Glyphicon Table
                                                             Tabs Tab]]
             [cljs-http.client :as http]
             [clojure-web.components.common :refer [show-on-click show-when select-to-show
                                                    call-method]]))


(defn assign-table [& {:keys [data show?]}]
  (dispatch [:resources {:limit 10000}])
  (let [submit-fn (fn [data]
                    (let  [url (str "/roles/" (:id @data) "/resources")]
                      (go
                        (<! (http/put  url {:form-params (dissoc @data :resources)}))
                        (reset! data {}))))
        resources (subscribe [:resources])
        process-ok (fn [data]
                     (submit-fn data)
                     (reset! show? false))
        state (reagent/atom true)]
    (show-when
     (reagent/create-class
      {:component-did-mount
       (fn []
         (->> (:resources @data)
              (map (fn [e]
                     (let [chk (js/$ (str ":checkbox[value=" (:resource-id e) "]"))
                           a   (js/$ (str "#" (:resource-id e) ))]
                       (.prop chk "checked" true)
                       (.text (js/$ a) (:scope e))
)))
              (doall))
         (.editable (js/$ "#resources a")
                    (clj->js {
                              :type "select"
                              :title "Select scope"
                              :placement "right"
                              :value "system"
                              :source [{:value "system" :text "system"}
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
                  [Button {:on-click (fn []
                                       (let [checked (js/$ "input:checked")]
                                         (goog.array.forEach
                                          checked (fn [val index arr]
                                                    (let [a (.find (.parent (.parent (js/$ val))) "a")
                                                          resource-id (.attr (js/$ a) "id")
                                                          scope (.html (js/$ a))]
                                                      (when-not (nil? resource-id)
                                                        (swap! data assoc resource-id scope)))))
                                         (dispatch [:reset-role-ress])
                                         (process-ok data)))}
                   "Save"]]))})
     resources)))

(def role-panel (create-bs-table
                 :entity "role"
                 :btns {
                        "get-role-resources"
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
                                         (merge body :data (atom {:id (:id @row)
                                                                  :resources @data})))
                         :selected-on-hide #(dispatch [:reset-role-ress])
                         :body [assign-table]]}))
