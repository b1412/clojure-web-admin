(ns clojure-web.routes.role
  (:require [clojure-web.common.crud :refer [defcrud-routes query-entity create-entity]]
            [clojure-web.common.fingerprint :as fingerprint]
            [clojure-web.db.entity :refer [role role-resource] ]
            [clojure-web.render :as render]
            [clojure.set :as set]
            [compojure.api.sweet :refer [context* GET* PUT*]]
            [ring.util.http-response :refer [ok]]
            [korma.core :as k]
            [taoensso.timbre :as log]))

(defn assign-resources-to-role
  [id param]
  (fingerprint/init! role-resource {:role_id id})
  (->> (dissoc param :current-user :s-scope)
       (map (fn [[res-id s]] {:resource-id res-id :scope s :role-id id}))
       (map (partial create-entity role-resource))
       (doall))
  (fingerprint/remove-expired! role-resource))


(defcrud-routes role-routes role
  (context* "/:id"  [id]
            (PUT* "/resources"
                  {params :params}
                  (ok (assign-resources-to-role id (dissoc params :id))))
            (GET* "/resources"
                  {params :params}
                  (render/json (:rows (query-entity role-resource {:entity "role-resource" :role-id id :limit 1000}))))))



