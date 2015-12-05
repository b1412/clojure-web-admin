(ns clojure-web.routes.role
  (:require [clojure-web.common.crud :refer [defcrud-routes query-entity]]
            [clojure-web.db.entity :refer [role role-resource]]
            [clojure-web.render :as render]
            [clojure.set :as set]
            [compojure.api.sweet :refer [context* GET* PUT*]]
            [korma.core :as k]
            [taoensso.timbre :as log]))

(defn assign-resources-to-role
  "Renew the permissions of the role:
     1 Get all the resources of current role.
     2 Delete the resources whose id is not in  params.
     3 Update the scope whose id is in db-resources  but scope is different.
     4 Create new resources in parmas whose id is not in the resources  for the role 
  "
  [id param]
  (let [param (dissoc param :current-user :s-scope)
        db-ress (->> (-> (k/select* role-resource)
                         (k/where {:role_id id})
                         (k/select))
                     (map (juxt :resource-id :scope)))
        db-ids (->> db-ress
                    (map (fn [[r-id s]] r-id))
                    (set))
        param-ids (->> param
                       (map (fn [[r-id s]] (Integer/parseInt r-id)))
                       (set))
        del-data  (->> db-ress
                       (remove (fn [[r-id s]] (param-ids r-id))))
        exist-data (->> param
                        (filter (fn [[r-id s]]
                                  (let [curr (->> db-ress
                                                  (filter (fn [[db-r-id db-s]]
                                                            (= (Integer/parseInt r-id) db-r-id)))
                                                  (first))]
                                    (and (db-ids (Integer/parseInt r-id))
                                         (not= s (second curr)) )))))
        new-data (->> param
                      (remove (fn [[r-id s]] (db-ids (Integer/parseInt r-id)))))]
    (->> del-data
         (map (fn [[r-id s]] (-> (k/delete* role-resource)
                                 (k/where {:role_id id :resource_id r-id :scope s})
                                 (k/delete))))
         (doall))

    (->> new-data
         (map (fn [[r-id s]] [:resource-id r-id :scope s :role-id id]))
         (doall)
         (map (partial apply hash-map))
         (#(-> (k/insert* role-resource)
               (k/values %)
               (k/insert))))

    (->> exist-data
         (map (fn [[r-id s]]
                (-> (k/update* role-resource)
                    (k/set-fields {:scope s})
                    (k/where {:role_id id
                              :resource_id r-id})
                    (k/update))))
         (doall))
    (render/json {:message "success"})))


(defcrud-routes role-routes role
  (context* "/:id"  [id]
            (PUT* "/resources"
                  {params :params}
                  (assign-resources-to-role id (dissoc params :id)))
            (GET* "/resources"
                  {params :params}
                  (render/json (:rows (query-entity role-resource {:entity "role-resource" :role-id id :limit 1000}))))))



