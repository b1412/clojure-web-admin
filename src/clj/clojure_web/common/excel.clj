(ns clojure-web.common.excel
  (:require
   [clojure-web.metadata-kit :refer [has-feature?]]
   [clojure-web.db.entity
    :refer
    [get-all-columns-with-comment]]
   [excel-templates.build :refer [render-to-file]]
   [taoensso.timbre :as log]
   [korma.core :refer [select]]))

(defn to-excel-data [entity-data columns]
  (->> entity-data
       (mapv (apply juxt (map keyword columns)))))

(defn export-file [entity]
  (let [entity-data (select entity)
        columns (->> (get-all-columns-with-comment (:table entity))
                     (filter (partial has-feature? :exportable))
                     (map (comp name :column-name))
                     (reduce conj []))
        excel-data (to-excel-data entity-data columns)]
    (log/info excel-data)
    (render-to-file
     "entity-template.xlsx"
     (str "/tmp/" (:table entity) ".xlsx")
     {"Entity" {0 [columns]
                1 excel-data}})))


(defn export-excel-template [entity]
  (let [entity-data (select entity)
        columns (->> (get-all-columns-with-comment (:table entity))
                     (filter (partial has-feature? :importable))
                     (map (comp name :column-name))
                     (reduce conj []))]
    (render-to-file
     "empty.xlsx"
     (str "/tmp/" (:table entity) "-template.xlsx")
     {"Entity" {0 [columns]}})))
