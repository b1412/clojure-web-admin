(ns clojure-web.components.bs-table
  (:require-macros [cljs.core.async.macros :refer [go]]
                   [reagent.ratom :refer [reaction]])
  (:require [cljs-http.client :as http]
            [cljs.core.async :refer [<!]]
            [reagent.core  :as    reagent :refer [atom render-component]]
            [clojure-web.uploads :refer [bs-file-upload]]
            [clojure-web.components.common :refer [toolbar renderable?
                                                   td-value make-kw
                                                   th-value call-method
                                                   show-when has-permission
                                                   show-on-click
                                                   select-to-show
                                                   select-to-do
                                                   FaIcon]]
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

(defn lookup-entity-btn [& {:keys [lookup-table lookup-label metadatas form-data column-name show?]}]
  [select-to-do
   :trigger [Button {:bs-style "primary"}
             [Glyphicon {:glyph "search"}] "Lookup"]
   :select-fn (fn []
                (->>
                 (call-method lookup-table "getSelections")
                 (first)
                 (js->clj)))
   :confirm-fn  (fn [row]
                  (swap! form-data assoc column-name  (row "id"))
                  (swap! form-data assoc (keyword (str lookup-table "." (str lookup-label))) (row (str lookup-label)) )
                  (reset! show? false))])

(defn lookup-entity-bs-table [& {:keys [lookup-table lookup-label btns form-data column-name show?]}]
  (let [permissions (subscribe [:permissions])
        entity-metadata  (make-kw lookup-table "-metadata")
        get-entity-metadata (make-kw "get-" lookup-table "-metadata")
        entities (make-kw (plural lookup-table))
        rows (subscribe [entities])
        metadatas (subscribe [entity-metadata])
        table-show? (reaction (not-empty @metadatas))
        entity-btns {(str "delete-" lookup-table)
                     [lookup-entity-btn
                      :metadatas metadatas
                      :lookup-label lookup-label
                      :lookup-table lookup-table
                      :form-data form-data
                      :column-name column-name
                      :show? show?]}
        btns (merge entity-btns btns)
        anonymous-btns (filter (fn [[permission btn]] (= :anonymous permission)) btns)
        protected-btns (remove (fn [[permission btn]] (= :anonymous permission)) btns)]

    (dispatch [get-entity-metadata])
    (fn []
      (when @table-show?
        [bs-table
         :data-url (str  "/" (plural lookup-table) "/")
         :metadata @metadatas
         :entity lookup-table
         :search-box [search-box
                      :metadatas @metadatas
                      :entity lookup-table]
         :toolbar [toolbar
                   :children (->> protected-btns
                                  (map (fn [[k v]]
                                          [has-permission
                                           @permissions
                                           k v]))
                                  (concat anonymous-btns)
                                  (vec))]
         metadatas]))))
