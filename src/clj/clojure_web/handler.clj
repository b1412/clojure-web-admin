(ns clojure-web.handler
  (:require [clojure-web
             [exceptions :as ex]
             [middleware :as middleware]
             [render :as render]]
            [clojure-web.routes
             [brand :refer [brand-routes]]
             [computer :refer [computer-routes]]
             [home :refer [home-routes]]
             [organization :refer [organization-routes]]
             [resource :refer [resource-routes]]
             [role :refer [role-routes]]
             [upload :refer [upload-routes]]
             [user :refer [user-routes]]]
            [compojure.route :as route]
            [environ.core :refer [env]]
            [compojure.api.sweet :refer [defapi defroutes* swagger-docs]]
            [ring.swagger.ui :refer [swagger-ui]]
            [taoensso.timbre :as timbre]
            [taoensso.timbre.appenders.3rd-party.rotor :as rotor]))

(defn init
  "init will be called once when
   app is deployed as a servlet on
   an app server such as Tomcat
   put any initialization code here"
  []

  (timbre/merge-config!
   {:level (if (env :dev) :trace :info)
    :appenders {:rotor (rotor/rotor-appender
                        {:path "clojure_web.log"
                         :max-size (* 512 1024)
                         :backlog 10})}})

  (timbre/info (str
                "\n-=[clojure-web started successfully"
                (when (env :dev) " using the development profile")
                "]=-")))

(defn destroy
  "destroy will be called when your application
   shuts down, put any clean up code here"
  []
  (timbre/info "clojure-web is shutting down...")
  (timbre/info "shutdown complete!"))

(defn sym-handler [^Exception e data request]
  (timbre/error e)
  {:status 500
   :headers {"Content-Type" "text/html"}
   :body (:message (read-string (subs (.getMessage e) 8)))})


(defapi app-routes
  {:exceptions
   {:handlers {ex/unauthorized-operation
               sym-handler
               :compojure.api.exception/default
               sym-handler}}}
  (swagger-ui  "/api-ui")
  (swagger-docs {:info {:title "Clojure Web API"}})
  brand-routes
  computer-routes
  brand-routes
  home-routes
  upload-routes
  organization-routes
  user-routes
  role-routes
  resource-routes
  (route/not-found
   (render/render404)))

(def app (middleware/wrap-base app-routes))
