(ns clojure-web.handlers
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs-http.client :as http]
            [cljs.core.async :refer [<!]]
            [clojure-web.reagent-utils :refer [reg-get-hdl]]
            [re-frame.core :refer [dispatch subscribe]]
            [clojure-web.reagent-utils :refer [reg-sub-entity reg-handler-entity reg-reset-hdl]]
            [re-frame.core :refer [register-handler path trim-v after debug]]))


;; -- Middleware --------------------------------------------------------------


(def middlewares [trim-v debug])        ;; remove event id from event vec



;; -- Handlers ----------------------------------------------------------------



(def entities [ "organization" "user" "role" "resource" "computer"])

(->> entities
     (mapcat (juxt reg-sub-entity reg-handler-entity))
     (doall))

(reg-get-hdl :permissions (fn [params] "/permissions"))

(reg-get-hdl :role-ress (fn [[id]]
                          (str "/roles/" id "/resources")))

(reg-reset-hdl :role-ress)



(register-handler
 :add-tab
 middlewares
 (fn [db [new-tab]]
   (let [tabs (get-in db [:tabs])
         new-tabs (if-not (some #{new-tab} tabs)
                    (conj tabs new-tab)
                    tabs)]
     (assoc db :tabs new-tabs))))

(register-handler
 :selected-tab-id
 middlewares
 (fn [db [id]]
   (assoc db :selected-tab-id id)))
