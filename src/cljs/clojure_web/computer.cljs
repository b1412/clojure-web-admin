(ns clojure-web.computer
  (:require-macros [cljs.core.async.macros :refer [go]]
                   [secretary.core         :refer [defroute]])
  (:require [clojure-web.components.entity-bs-table
             :refer
             [create-bs-table]]
            [clojure-web.components.react-bootstrap
             :refer [Button ButtonToolbar
                     Glyphicon Table
                     Tabs Tab]]
            [inflections.core :refer  [plural titleize]]
            [clojure-web.components.reforms :refer [form]]
            [clojure-web.components.common
             :refer [show-on-click show-when select-to-show
                     call-method alert dialog]]
            [cljs-http.client :as http]))



(defn details-view [& {:keys [master details]}]
  [:div master
   [Tabs
    (for [detail details]
      [Tab {:title (str "Detail Table" detail) :event-key detail}
       [create-bs-table
        :entity
        detail]])]])


(defn master-detail [& {:keys [entity metadatas]}]
  (let [form-data (atom {})]
    [select-to-show
     :title (str "Edit " (titleize entity))
     :trigger [Button {:bs-style "primary"}
               [Glyphicon {:glyph "edit"}] "Edit"]
     :select-fn (fn []
                  (->>
                   (call-method entity "getSelections")
                   (first)
                   (js->clj)
                   (map (fn [[k v]] [(keyword k) v]))
                   (into {})))
     :load-row-fn (fn [body row]
                    (merge body :form-data row))

     :body [details-view
            :details
            ["role","brand"]
            :master
            [form
             :submit-fn (fn [data]
                          (go (let [url (str "/" (plural entity) "/" (:id @data))
                                    res (<! (http/put
                                             url
                                             {:form-params
                                              (->> (dissoc @data :id :errors)
                                                   (filter (fn [[k v]] (not-empty (str v))))
                                                   (into {}))}))]
                                (if (:success res)
                                  (dialog :message "Updated successfully")
                                  (dialog :message (:body res) :type (aget js/BootstrapDialog "TYPE_DANGER")))
                                (call-method entity "refresh")
                                (reset! data {}))))
             :metadata  metadatas
             :form-data form-data
             :entity    entity]]]))

(def computer-panel (create-bs-table
                     :entity
                     "computer"
                     :btns
                     {:anon [master-detail]}))
