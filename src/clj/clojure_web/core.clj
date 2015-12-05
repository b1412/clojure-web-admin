(ns clojure-web.core
  (:require [clojure-web.handler :refer [app destroy init]]
            [environ.core :refer [env]]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.reload :as reload])
  (:gen-class))

(defonce server (atom nil))

(defn parse-port [port]
  (Integer/parseInt (or port (str (env :port)) "3000")))

(defn start-server
  ([] (start-server 3000))
  ([port]
   (init)
   (reset! server
           (run-jetty
            (if (env :dev) (reload/wrap-reload #'app) app)
            {:port port
             :join? false}))))

(defn stop-server []
  (when @server
    (destroy)
    (.stop @server)
    (reset! server nil)))

(defn restart []
  (stop-server)
  (start-server))

(defn -main [& [port]]
  (let [port (parse-port port)]
    (.addShutdownHook (Runtime/getRuntime) (Thread. stop-server))
    (start-server port)))
