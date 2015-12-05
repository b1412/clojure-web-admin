(ns clojure-web.reagent-utils
  (:require-macros [reagent.ratom :refer [reaction]]
                   [cljs.core.async.macros :refer [go]])
  (:require [re-frame.core :refer [register-sub register-handler dispatch
                                    path trim-v after debug]]
            [cljs.core.async :refer [<!]]
            [cljs-http.client :as http]
            [inflections.core :refer  [plural]]))

(def middlewares [trim-v])        ;; remove event id from event vec


(defn reg-sub [k]
  []
  (register-sub
   k
   (fn [db _]
     (reaction (k @db)))))

(defn reg-subs [& subs]
  (->> subs
       (map reg-sub)
       (doall)))

(defn reg-sub-entity [entity-name]
  (let [plurality (keyword (plural  entity-name ))
        current-entity (keyword (str "current-" entity-name))
        entity-metadata (keyword (str entity-name "-metadata"))]
    (reg-subs plurality
              current-entity
              entity-metadata)))


;;-------------------handler helper-----------------


(defn reg-reset-hdl [k]
  (register-handler
   (keyword (str "reset-" (name k)))
   middlewares
   (fn [db _]
     (dissoc db (keyword k)))))

(defn reg-get-hdl [k url-fn]
  (let [event-name (name k)
        get-event (keyword (str "get-" event-name))
        get-success-event (keyword (str "get-" event-name "-success"))]
    (register-handler
     get-event
     middlewares
     (fn [db params]
       (go (dispatch
            [get-success-event
             (:body (<! (http/get (url-fn params))))]))
       db))

    (register-handler
     get-success-event
     middlewares
     (fn [db [data]]
       (assoc db k data)))))


(defn reg-handler-entity [entity-name]
  (let [plurality (plural entity-name)
        current-entity (keyword (str "current-" entity-name))
        base-url (str "/" plurality "/")
        entity-metadata (keyword (str entity-name "-metadata"))
        add-entity (keyword (str "add-" entity-name))
        update-entity (keyword (str "update-" entity-name))
        delete-entity (keyword (str "delete-" entity-name))
        get-entity (keyword (str "get-" entity-name))
        get-entity-success (keyword (str "get-" entity-name "-success"))
        get-entity-metadata (keyword (str "get-" entity-name "-metadata"))
        get-entity-metadata-success (keyword (str "get-" entity-name "-metadata-success"))
        entity-success (keyword (str plurality "-success"))]

    (reg-reset-hdl plurality)
    (register-handler
     get-entity-metadata
     (fn [db _]
       (go (dispatch
            [get-entity-metadata-success
             (:body (<! (http/get (str "/" plurality "/meta"))))]))
       db))

    (register-handler
     get-entity-metadata-success
     (fn [db [_ data]]
       (assoc db entity-metadata data)))

    (register-handler
     (keyword plurality)
     (fn [db [_ conds]]
       (go (dispatch
            [entity-success
             (:body  (<! (http/get base-url {:query-params conds})))]))
       db))

    (register-handler
     entity-success
     (fn [db [_ data]]
       (assoc db (keyword plurality) (:rows data))))

    (register-handler
     get-entity
     (fn [db [_ id]]
       (go (dispatch
            [get-entity-success
             (:body (<! (http/get (str base-url id))))]))
       db))

    (register-handler
     get-entity-success
     (fn [db [_ e]]
       (assoc db current-entity e)))

    (register-handler
     add-entity
     (fn [db [_ data]]
       (go
         (prn "add-entity")
         (prn (:data data))
         (<! (http/post base-url
                        {:json-params (:data data)}))
         (dispatch [(keyword plurality)]))
       db))
    (register-handler
     update-entity
     (fn [db [_ data]]
       (go
         (<! (http/put (str base-url (:id data))
                       {:json-params (:data data)}))
         (dispatch [(keyword plurality)]))
       db))

    (register-handler
     delete-entity
     (fn [db [_ id]]
       (go (<! (http/delete (str base-url id)))
           (dispatch [(keyword plurality)]))
       db))))
