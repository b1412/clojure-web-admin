(ns clojure-web.db.kit
  (:require [clojure-web.db.entity :refer [resource role-resource]]
            [inflections.core :refer [plural titleize]]
            [korma.core :as k]))

(defn get-res-for-entity
  "Generate basic resources of an entity like follows
  +----+----------------+--------+-------------------+
  | id | uri            | method | desc              |
  +----+----------------+--------+-------------------+
  |  1 | /tasks/.*      | GET    | query  tasks      |
  |  2 | /tasks/        | POST   | create a new task |
  |  3 | /tasks/[0-9]*  | DELETE | delete a task     |
  |  4 | /tasks/[0-9]*  | PUT    | update a task     |
  |  5 | /tasks/columns | GET    | get task columns  |
  +----+----------------+--------+-------------------+
  "
  [entity]
  (let [table (:table entity)
        plural-table (plural table )]
    [{:uri (str "/" (plural table) "[/]?")
      :method "GET"
      :desc (str "query " (plural table ))
      :key  plural-table
      :type "menu"
      :label (titleize table)
      :parent-id 0
      :entity table}
     {:uri (str "/" plural-table "[/]?")
      :method "POST"
      :desc (str "create a new " table)
      :key (str "new-" table)
      :type "button"
      :label "New"
      :entity table}
     {:uri (str "/" (plural table) "/[0-9]+")
      :method "GET"
      :desc (str "get a specific" table )
      :key (str "get-" table)
      :type "button"
      :label "Edit"
      :entity table}
     {:uri (str "/" plural-table "/[0-9]+")
      :method "DELETE"
      :desc (str "delete a specific " table)
      :key (str "delete-" table)
      :type "button"
      :label "Delete"
      :entity table}
     {:uri (str "/" plural-table "/[0-9]+")
      :method "PUT"
      :key (str "edit-" table)
      :desc (str "update a specific " table)
      :type "button"
      :label "Update"
      :entity table}
     {:uri (str "/" plural-table "/meta")
      :method "GET"
      :key (str table "-meta")
      :desc (str "get metadata of " table)
      :type "button"
      :label "Metadata"
      :entity table}
     {:uri (str "/" plural-table "/charts")
      :method "GET"
      :key (str  table "-charts")
      :desc (str "view " table " charts")
      :label "Charts"
      :type "button"
      :entity table}
     {:uri (str "/" plural-table "/excel")
      :method "POST"
      :key (str "import-" table "-excel")
      :desc (str "import  excel of " (plural table))
      :type "button"
      :label "Import"
      :entity table}
     {:uri (str "/" plural-table "/excel")
      :method "GET"
      :key (str "export-" table "-excel")
      :desc (str "export  excel of " (plural table))
      :type "button"
      :label "Export"
      :entity table}
     {:uri (str "/" plural-table "/excel/template")
      :method "GET"
      :key (str "export-" table "-excel-template")
      :desc (str "get excel template of " (plural table))
      :type "button"
      :entity table}]))

(defn insert-ress
  [ress]
  (let [new-keys (->> ress
                      (map #(-> (k/insert* resource)
                                (k/values %)
                                (k/insert)))
                      (map :generated-key))
        parent (first new-keys)
        children (rest new-keys)]
    (->> children
         (map #(-> (k/update* resource)
                   (k/set-fields {:parent_id parent})
                   (k/where {:id %})
                   (k/update))))))

(defn insert-res-for-entity [entity]
  (->> (get-res-for-entity entity)
       (insert-ress)))

(defn get-res-to-role [entity role-id]
  (let [res (k/select resource (k/where {:entity (:table entity)}))]
    (->> res
         (map (comp
               (partial merge {:role_id role-id
                               :scope "system"})
               #(hash-map :resource_id (:id %)))))))

(defn insert-res-to-role
  [entity role-id]
  (let [data (get-res-to-role entity role-id)]
    (k/insert role-resource (k/values data))))
