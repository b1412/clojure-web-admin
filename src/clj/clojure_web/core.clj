(ns clojure-web.core
  (:require [clojure-web.handler :refer [app destroy init]]
            [environ.core :refer [env]]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.reload :as reload]
            [environ.core :refer [env]]
;;            [cemerick.piggieback :as piggieback]
;;            [weasel.repl.websocket :as weasel]
;;            [figwheel-sidecar.auto-builder :as fig-auto]
;;            [figwheel-sidecar.core :as fig]
;;            [clojurescript-build.auto :as auto]
            )
  (:gen-class))

(def is-dev? (env :dev))

(comment (defn browser-repl []
    (let [repl-env (weasel/repl-env :ip "0.0.0.0" :port 9001)]
      (piggieback/cljs-repl :repl-env repl-env))))

(comment (defn start-figwheel []
    (let [server (fig/start-server { :css-dirs ["resources/public/css"] })
          config {:builds [{:id "dev"
                            :source-paths ["src/cljs" "env/dev/cljs"]
                            :compiler {:output-to            "resources/public/js/app.js"
                                       :output-dir           "resources/public/js/out"
                                       :source-map           true
                                       :optimizations        :none
                                       :source-map-timestamp true
                                       :preamble             ["react/react.min.js"]}}]
                  :figwheel-server server}]
      (fig-auto/autobuild* config))))


(defonce server (atom nil))

(defn parse-port [port]
  (Integer/parseInt (or port (str (env :port)) "3000")))

(defn start-server
  ([] (start-server 3000))
  ([port]
;   (if (env :dev) (start-figwheel))
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
