(ns clojure-web.components.entity-bs-table
  (:require-macros [cljs.core.async.macros :refer [go]]
                   [reagent.ratom :refer [reaction]])
  (:require [bouncer.core :as b]
            [clojure-web.validate :refer [get-validators]]
            [cljs-http.client :as http]
            [cljs.core.async :refer [<!]]
            [shodan.console :as console :include-macros true]
            [reagent.core   :as reagent :refer [atom render-component]]
            [clojure-web.uploads :refer [bs-file-upload]]
            [clojure-web.components.common :refer [toolbar renderable?
                                                   td-value make-kw
                                                   th-value call-method
                                                   show-when has-permission
                                                   show-on-click
                                                   select-to-show
                                                   select-to-do
                                                   alert
                                                   dialog
                                                   close
                                                   FaIcon]]
            [clojure-web.components.reforms :refer [form]]
            [clojure-web.components.search-box :refer [search-box]]
            [clojure-web.components.react-bootstrap :refer [Button
                                                            ButtonToolbar
                                                            Glyphicon
                                                            Modal
                                                            ModalBody
                                                            ModalHeader
                                                            ModalFooter
                                                            ModalTitle]]
            [clojure-web.components.bs-table :refer [bs-table]]
            [re-frame.core :refer [dispatch subscribe]] 
            [superstring.core :as str]
            [inflections.core :refer  [plural titleize]]
            [clojure.set :as set]))




(defn render-charts [metadatas entity entities]
  (let [label (->> metadatas
                   (filter #(= 1 (:chart-label % 0))) 
                   (first)
                   (:column-name)
                   (keyword))
        value (->> metadatas
                   (filter #(= 1 (:chart-value % 0)))
                   (first)
                   (:column-name)
                   (keyword))
        cd {:title {:text (str (titleize entity) " " (name value) " in " (name label))}
            :data [{:type "column"
                    :dataPoints
                    (->> entities
                         (map (fn [e] {:label (label e)
                                       :y (value e)}))
                         (vec))}]}]
    (reagent/create-class
     {:component-did-mount
      (fn [] (.render (new js/CanvasJS.Chart "chartContainer" (clj->js cd))))
      :reagent-render
      (fn []
        [:div#chartContainer])})))

(defn canvas-chart [& {:keys [entity metadatas]}]
  (dispatch [(make-kw (str (plural entity)))])
  (let [entities (subscribe [(make-kw (plural entity))])
        show? (reaction (not-empty @entities))]
    (fn []
      (when @show?
        [render-charts
         @metadatas
         entity
         @entities]))))

(defn excel-form [& {:keys [entity show?]}]
  (when @show?
    [:div
     [Button {:bs-style "info"
              :on-click
              (fn [e] (aset js/window "location" (str "/" (plural entity) "/excel/template" )))}
      [FaIcon "download"] "Download the excel template"]
     [bs-file-upload
      :attr  {:class "file"
              :data-allowed-file-extensions "[\"xls\", \"xlsx\"]"
              :type "file"
              :name "file"}
      :options {:uploadUrl (str "/" (plural entity) "/excel")
                :maxFileCount 1
                :overwriteInitial false}
      :events  {"fileuploaded"
                (fn [event data preview-id index]
                  (call-method entity "refresh")
                  (reset! show? false))}]]))


(defn new-entity-btn [& {:keys [entity metadatas]}]
  (let [form-data (atom {})
        create-fn (fn [data]
                    (let [url (str "/" (plural entity) "/")]
                      (go (let [res (<! (http/post url {:form-params (dissoc data :errors)}))]
                            (if (:success res)
                                 (dialog :message "Created successfully")
                                 (dialog :message (:body res) :type (aget js/BootstrapDialog "TYPE_DANGER")))
                            (call-method entity "refresh")
                           ))))]
    [show-on-click
     :title (titleize (str "New " entity))
     :trigger
     [Button {:bs-style "primary"}
      [Glyphicon {:glyph "plus"}] "New"]
     :body [form
            :metadata  metadatas
            :form-data form-data
            :entity    entity]
     :footer [[Button
               {:bs-style "primary"
                :on-click
                (fn []
                  (let [validators (get-validators @metadatas)
                        result (apply (partial b/validate @form-data) validators)
                        errs (get result 0)]
                    (if errs
                      (do (->> errs
                             (map (fn [[k v]]
                                    (swap! form-data assoc-in [:errors k] (first v))))
                             (doall))
                          nil)
                      (do
                        (create-fn @form-data)
                        (reset! form-data {})))))}
               "Save"]
              [Button {} "Cancel"]]]))

(defn delete-entity-btn [& {:keys [entity metadatas]}]
  [select-to-do
   :trigger [Button {:bs-style "primary"}
             [Glyphicon {:glyph "minus"}] "Delete"]
   :select-fn (fn []
                (->>
                 (call-method entity "getSelections")
                 (first)
                 (js->clj)))
   :confirm-fn (fn [row]
                 (go (let [res (<! (http/delete (str "/"(plural entity) "/" (row "id"))))]
                       (if (:success res)
                         (dialog :message "Deleted successfully")
                         (dialog :message (:body res) :type (aget js/BootstrapDialog "TYPE_DANGER")))
                       (call-method entity "refresh"))))])

(defn edit-entity-btn [& {:keys [entity metadatas]}]
  (let [form-data (atom {})
        submit-fn (fn [data]
                    (go (let [url (str "/" (plural entity) "/" (:id data))
                              res (<! (http/put
                                       url
                                       {:form-params
                                        (->> (dissoc data :id :errors)
                                             (filter (fn [[k v]] (not-empty (str v))))
                                             (into {}))}))]
                          (if (:success res)
                            (dialog :message "Updated successfully")
                            (dialog :message (:body res) :type (aget js/BootstrapDialog "TYPE_DANGER")))
                          (call-method entity "refresh"))))
        validators (get-validators @metadatas)]
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
                    (reset! form-data @row)
                    body)

     :body [form
            :metadata  metadatas
            :form-data form-data
            :entity    entity]
     :footer
     [[Button
        {:bs-style "primary"
         :on-click
         (fn []
           (let [result (apply (partial b/validate @form-data) validators)
                 errs (get result 0)]
             (if errs
               (do
                 (->> errs
                      (map (fn [[k v]]
                             (swap! form-data assoc-in [:errors k] (first v))))
                      (doall))
                 nil)
               (do
                 (submit-fn  @form-data)
                 (reset! form-data {})))))}
       "Save"]
      [Button {} "Cancel"]]]))

(defn export-entity-btn [& {:keys [entity metadatas] }]
  [Button {:bs-style "primary"
           :on-click (fn [e]
                       (aset js/window "location"
                             (str "/" (plural entity) "/excel" ))
                       )}
   [Glyphicon {:glyph "export"}] "Export"])

(defn import-entity-btn [& {:keys [entity metadatas]}]
  [show-on-click
   :trigger
   [Button {:bs-style "primary"}
    [Glyphicon {:glyph "import"}] "Import"]
   :body [excel-form
          :entity entity]])

(defn entity-charts-btn [& {:keys [entity metadatas]}]
  [show-on-click
   :trigger [Button {:bs-style "primary"}
             [FaIcon "bar-chart"]
             "Charts"]
   :on-hide #(dispatch [(make-kw (str "reset-" (plural entity)))])
   :body [canvas-chart
          :entity entity
          :metadatas metadatas]])

(defn entity-bs-table [& {:keys [entity btns url]}]
  (let [permissions (subscribe [:permissions])
        entity-metadata  (make-kw entity "-metadata")
        get-entity-metadata (make-kw "get-" entity "-metadata")
        entities (make-kw (plural entity))
        rows (subscribe [entities])
        metadatas (subscribe [entity-metadata])
        show? (reaction (not-empty @metadatas))
        entity-btns {(str "new-" entity)
                     [new-entity-btn]
                     (str "delete-" entity)
                     [delete-entity-btn]
                     (str "edit-" entity)
                     [edit-entity-btn]
                     (str  "export-" entity "-excel")
                     [export-entity-btn]
                     (str  "import-" entity "-excel")
                     [import-entity-btn]
                     (str entity "-charts")
                     [entity-charts-btn]}
        btns (->> btns
                  (merge entity-btns)
                  (map (fn [[k v]] [k (merge v
                                            :entity entity
                                            :metadatas metadatas)]))
                  (into {}))
        anonymous-btns (filter (fn [[permission btn]] (= :anon permission)) btns)
        protected-btns (remove (fn [[permission btn]] (= :anon permission)) btns)]

    (dispatch [get-entity-metadata])
    (fn []
      (when @show?
        [bs-table
         :data-url (or url (str  "/" (plural entity)))
         :metadata @metadatas
         :entity entity
         :search-box [search-box
                      :metadatas @metadatas
                      :entity entity]
         :toolbar [toolbar
                   :children (->> protected-btns
                                  (map (fn [[k v]]
                                          [has-permission
                                           @permissions
                                           k v]))
                                  (concat anonymous-btns)
                                  (vec))]
         metadatas]))))

(defn create-bs-table [& {:keys [entity btns url]}]
  (fn[]
    [entity-bs-table
     :entity entity
     :url url
     :btns btns]))

