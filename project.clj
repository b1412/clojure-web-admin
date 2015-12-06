(defproject clojure-web "0.1.0-SNAPSHOT"
 
  :description "A metadata-driven clojure web admin app"
  :url "https://github.com/b1412/clojure-web-admin"
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/tools.trace "0.7.9"]
                 [org.clojure/core.cache "0.6.4"]
                 [clj-time "0.11.0"]
                 [clojure-humanize "0.1.0"]
                 [inflections "0.10.0"]
                 [defun "0.3.0-alapha"]
                 [korma "0.4.0"] 
                 [superstring "2.1.0"]
                 [slingshot "0.12.2"]
                 [com.taoensso/timbre "4.1.1"]
                 [pandect "0.5.4"]
                 [environ "1.0.1"]
                 [com.infolace/excel-templates "0.3.1"]
                 [dk.ative/docjure "1.9.0"]
                 [ring-webjars "0.1.1"]
                 [cljsjs/react "0.14.0-1"]
                 [cljsjs/react-bootstrap "0.25.1-0"]
                 [org.webjars/bootstrap "3.3.5"]
                 [org.webjars/jquery "2.1.4"]
                 [org.webjars.bower/bootstrap-treeview "1.2.0"]
                 [org.webjars.bower/bootstrap-table "1.9.1"]
                 [org.webjars.bower/bootstrap-fileinput "4.2.7"]
                 [org.webjars.npm/react "0.14.2"]
                 [org.webjars.bower/eonasdan-bootstrap-datetimepicker "4.17.37"]
                 [ring/ring-defaults "0.1.5"]
                 [ring "1.4.0"]
                 [metosin/ring-middleware-format "0.6.0"]
                 [metosin/ring-http-response "0.6.5"]
                 [bouncer "0.3.3"]
                 [prone "0.8.2"]
                 [org.clojure/tools.nrepl "0.2.11"]
                 [org.clojure/clojurescript "1.7.145" :scope "provided"]
                 [shodan "0.4.2"]
                 [spellhouse/clairvoyant "0.0-72-g15e1e44"]
                 [org.clojure/tools.reader "0.9.2"]
                 [reagent "0.5.1"]
                 [re-frame "0.4.1"]
                 [re-com "0.7.0"]
                 [reagent-forms "0.5.13"]
                 [secretary "1.2.3"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [metosin/compojure-api "0.24.0"]
                 [metosin/ring-swagger-ui "2.1.2"]
		 [org.clojure/data.json "0.2.6"]
                 [hiccup "1.0.5"]
                 [cljs-http "0.1.37"]
                 [hiccup-bridge "1.0.1"]
                 [crypto-password "0.1.3"]
                 [mysql/mysql-connector-java "5.1.6"]]
  :source-paths ["src/clj" "src/cljc"]
  :min-lein-version "2.0.0"
  :uberjar-name "clojure-web.jar"
  :jvm-opts ["-server" "-Duser.timezone=UTC" "-XX:-OmitStackTraceInFastThrow"]
  :main clojure-web.core
  :plugins [[lein-environ "1.0.1"]
            [hiccup-bridge "1.0.1"]
            [lein-cljsbuild "1.1.0"]]
  :cljfmt {}
  :clean-targets ^{:protect false}
  [:target-path [:cljsbuild :builds :app :compiler :output-dir]
   [:cljsbuild :builds :app :compiler :output-to]]
  :cljsbuild
  {:builds
   {:app
    {:source-paths ["src/cljs" "src/cljc"]
     :compiler
     {:output-to "resources/public/js/app.js"
      :externs ["react/externs/react.js"]
      :pretty-print true}}}}
  :global-vars {*warn-on-reflection* false}
  :profiles
  {:uberjar {:omit-source true
             :env {:production true}
              :hooks [leiningen.cljsbuild]
              :cljsbuild
              {:jar true
               :builds
               {:app
                {:source-paths ["env/prod/cljs"]
                 :compiler {:optimizations :advanced :pretty-print false}}}} 
             
             :aot :all}
   :dev           [:project/dev :profiles/dev]
   :test          [:project/test :profiles/test]
   :project/dev  {:dependencies [[ring/ring-mock "0.3.0"]
                                 [ring/ring-devel "1.4.0"]
                                 [pjstadig/humane-test-output "0.7.0"]
                                 [lein-figwheel "0.4.1"]
                                 [midje "1.6.3"]]
                  :plugins [[lein-figwheel "0.4.1"]]
                   :cljsbuild
                   {:builds
                    {:app
                     {:source-paths ["env/dev/cljs"] :compiler {:source-map true}}}} 
                  
                  :figwheel
                  {:http-server-root "public"
                   :server-port 3449
                   :nrepl-port 7002
                   :css-dirs ["resources/public/css"]
                   :ring-handler clojure-web.handler/app}
                  
                  :repl-options {:init-ns clojure-web.core}
                  :injections [(require 'pjstadig.humane-test-output)
                               (pjstadig.humane-test-output/activate!)]
                  :env {:dev        true
                        :port       3000
                        :nrepl-port 7000}}
   :project/test {:env {:test       true
                        :port       3001
                        :nrepl-port 7001}}})
