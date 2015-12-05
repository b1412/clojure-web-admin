(ns clojure-web.components.bs-table
  (:require-macros [cljs.core.async.macros :refer [go]]
                   [reagent.ratom :refer [reaction]])
  (:require [cljs-http.client :as http]
            [cljs.core.async :refer [<!]]
            [shodan.console :as console :include-macros true]
            [reagent.core  :as    reagent :refer [atom render-component]]
            [clojure-web.uploads :refer [bs-file-upload]]
            [clojure-web.components.common :refer [toolbar renderable?
                                                   td-value make-kw
                                                   th-value call-method
                                                   show-when has-permission
                                                   show-on-click
                                                   select-to-show
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
            [re-frame.core :refer [dispatch subscribe]]
            [superstring.core :as str]
            [inflections.core :refer  [plural titleize]]
            [clojure.set :as set]))



(defn metadata->bst-data [metadata]
  (->> metadata
       (filter #(renderable? % '(:hidden :primary-key :hidden-in-grid)))
       (map (juxt th-value :column-name))
       (map (fn [[th field]]
               (hash-map :field field
                         :title th
                         :align "center"
                         :valign "middle"
                         :sortable true
                         :formatter (fn [value row index]
                                      (let [metadata-item
                                            (->> metadata
                                                 (filter #(= (:column-name %) field))
                                                 (first))
                                            row (->> (js->clj row)
                                                     (map (fn [[k v]] [(keyword k) v] ))
                                                     (into {}))]
                                        (td-value metadata-item (js->clj row)))))))))



(defn delete-entity [entity id]
  (let [url (str "/" (plural entity)  "/" id)]
    (go (let [res (<! (http/delete url))]
          (call-method entity "refresh")))))





(defn select-to-do
  [& {:keys [trigger
             select-fn
             alert-title alert-message
             confirm-title confirm-message
             confirm-fn]
      :or {alert-title "Notification" alert-message "Please select one record!"
           confirm-title "Notification" confirm-message "Are you sure?"}}]
  (let [selected-show? (reagent/atom false)
        no-selected-show? (reagent/atom false)
        args (atom {})]
    (fn []
      [:div
       [Button {:bs-style "primary"
                :on-click (fn [e]
                            (if-let [row (select-fn)]
                              (do (reset! selected-show? true)
                                  (reset! args row)
                                  (.preventDefault e) )
                              (do
                                (reset! no-selected-show? true)
                                (.preventDefault e))))}
        [Glyphicon {:glyph "minus"}] "Delete"]

       [Modal
        {:show @selected-show?
         :on-hide (fn [e]
                    (reset! selected-show? false)
                    (.preventDefault e) )
         :bs-size "large"
         :aria-labelledby "contained-modal-title-lg"}
        [ModalHeader {:close-button true} [ModalTitle confirm-title]]
        [ModalBody  confirm-message]
        [ModalFooter
         [Button
          {:on-click (fn [e]
                       (reset! selected-show? false)
                       (.preventDefault e) )}
          "No"]
         [Button
          {:bs-style "primary"
           :on-click (fn [e]
                       (confirm-fn @args)
                       (reset! selected-show? false)
                       (.preventDefault e) )}
          "Yes"]]]

       [Modal
        {:show @no-selected-show?
         :on-hide (fn [e]
                    (reset! no-selected-show? false)
                    (.preventDefault e))
         :bs-size "large"
         :aria-labelledby "contained-modal-title-lg"}
        [ModalHeader {:close-button true} [ModalTitle alert-title]]
        [ModalBody  alert-message]
        [ModalFooter
         [Button
          {:on-click (fn [e]
                       (reset! no-selected-show? false)
                       (.preventDefault e))}
          "Ok"]]]])))




(defn bs-table [& {:keys [options table-options
                          search-box toolbar
                          metadata entity data-url]}]

  (let [cols {:columns (vec (cons
                             {:field "state"
                              :radio true
                              :align "center"
                              :valign "middle"}
                             (metadata->bst-data metadata)))}]
    (reagent/create-class
     {:component-did-mount
      #(.bootstrapTable (js/$ (str "#" entity "-table"))
                        (clj->js cols))
      :reagent-render
      (fn []
        [:div.container
         [:div {:id (str entity "-toolbar")}
          search-box
          toolbar]
         [:table
          {:id (str entity "-table")
           :data-sort-order "desc"
           :data-side-pagination "server"
           :data-page-list "[10, 25, 50, 100, ALL]"
           :data-pagination "true"
           :data-click-to-select true
           :data-single-select true
           :data-toolbar (str "#" entity "-toolbar")
           :data-url data-url
           :data-id-field "id"}]])})))



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
                      (go (let [res (<! (http/post url {:form-params (dissoc @data :errors)}))]
                            (call-method entity "refresh")
                            (reset! data {})))))]
    [show-on-click
     :trigger
     [Button {:bs-style "primary"}
      [Glyphicon {:glyph "plus"}] "New"]
     :body [form
            :submit-fn create-fn
            :metadata  metadatas
            :form-data form-data
            :entity    entity]]))

(defn delete-entity-btn [& {:keys [entity metadatas]}]
  (let [delete-fn (fn [row]
                    (go (let [res (<! (http/delete (str "/"(plural entity) "/" (row "id"))))]
                          (call-method entity "refresh"))))]
    [select-to-do
     :select-fn (fn []
                  (->>
                   (call-method entity "getSelections")
                   (first)
                   (js->clj)))

     :confirm-fn delete-fn]))

(defn edit-entity-btn [& {:keys [entity metadatas]}]
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

     :body [form
            :submit-fn (fn [data]
                         (let  [url (str "/" (plural entity) "/" (:id @data))]
                           (go
                             (<! (http/put
                                  url
                                  {:form-params
                                   (->> (dissoc @data :id :errors)
                                        (filter (fn [[k v]] (not-empty (str v))))
                                        (into {}))}))
                             (call-method entity "refresh")
                             (reset! data {}))))
            :metadata  metadatas
            :form-data form-data
            :entity    entity]]))

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



(defn entity-bs-table [& {:keys [entity btns]}]
  (let [permissions (subscribe [:permissions])
        entity-metadata  (make-kw entity "-metadata")
        get-entity-metadata (make-kw "get-" entity "-metadata")
        entities (make-kw (plural entity))
        rows (subscribe [entities])
        metadatas (subscribe [entity-metadata])
        show? (reaction (not-empty @metadatas))
        entity-btns (merge {(str "new-" entity)
                            [new-entity-btn
                             :metadatas metadatas
                             :entity entity]
                            (str "delete-" entity)
                            [delete-entity-btn
                             :metadatas metadatas
                             :entity entity]
                            (str "edit-" entity)
                            [edit-entity-btn
                             :metadatas metadatas
                             :entity entity]
                            (str   "export-" entity "-excel")
                            [export-entity-btn
                             :entity entity
                             :metadatas metadatas]
                            (str   "import-" entity "-excel")
                            [import-entity-btn :entity entity
                             :metadatas metadatas]
                            (str entity "-charts")
                            [entity-charts-btn
                             :entity entity
                             :metadatas metadatas]})
        all-btns (merge entity-btns btns)
        anonymous-btns (filter (fn [[permission btn]] (= :anonymous permission)) all-btns)
        protected-btns (remove (fn [[permission btn]] (= :anonymous permission)) all-btns)]

    (dispatch [get-entity-metadata])
    (fn []
      (when @show?
        [bs-table
         :data-url (str  "/" (plural entity) "/")
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





(defn create-bs-table [& {:keys [entity btns]}]
  (fn[]
    [entity-bs-table
     :entity entity
     :btns btns]))
