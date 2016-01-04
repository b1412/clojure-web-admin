(ns clojure-web.middleware
  (:require [clojure-web
             [exceptions :as ex]
             [render :refer [*app-context*]]
             [session :as session]]
            [clojure-web.db.entity :refer [get-permissions]]
            [clojure-web.render :as render]
            [clojure.core.memoize :refer [lu]]
            [clojure.string :as str]
            [environ.core :refer [env]]
            [prone.middleware :refer [wrap-exceptions]]
            [ring.middleware
             [defaults :refer [site-defaults wrap-defaults]]
             [flash :refer [wrap-flash]]
             [format :refer [wrap-restful-format]]
             [reload :as reload]
             [webjars :refer [wrap-webjars]]]
            [ring.middleware.session.memory :refer [memory-store]]
            [taoensso.timbre :as log]

            [ring.util.response :refer [redirect]]
            [slingshot.slingshot :refer [throw+]])
  (:import javax.servlet.ServletContext))


(defn wrap-context [handler]
  (fn [request]
    (binding [*app-context*
              (if-let [context (:servlet-context request)]
                (try (.getContextPath ^ServletContext context)
                     (catch IllegalArgumentException _ context))
                (:app-context env))]
      (handler request))))

(defn wrap-dir-index [handler]
  (fn [req]
    (handler
     (update-in req [:uri]
                #(if (= "/" %) "/index.html" %)))))

(defn wrap-dev [handler]
  (if (env :dev)
    (-> handler
        reload/wrap-reload
        wrap-exceptions)
    handler))

(defn wrap-formats [handler]
  (wrap-restful-format handler {:formats [:json-kw :transit-json :transit-msgpack]}))


(defn cache-factory [cache-protocol & options]
  (fn [result]
    (apply cache-protocol result options)))

(def cache-helper (cache-factory lu :lu/threshold 3))


;;(def cached-authorities (cache-helper get-authorities))

(defn authorized? [{:keys [session uri request-method]} index-uri]
  (if-let [curr-user (:current-user session)]
    (->>  (get-permissions curr-user)
          (filter (fn [{:keys [method]} ] (= method (-> request-method (name) (str/upper-case)))))
          (map :uri)
          (map #(re-matches  (re-pattern %) uri))
          (#(if (or (not-every? nil? %) (= index-uri uri)) ::authorized ::un-authorized)))
    ::not-login))

(defn auth
  ([handler]
   (auth handler {}))
  ([handler options]
   (let [{:keys [login-uri logout-uri default-landing-uri
                 unauthorized-handler index-uri anon]}
         (merge {:login-uri "/login"
                 :logout-uri "/logout"
                 :index-uri "/"
                 :anon "(/uploads|/permissions|/api-ui)"
                 :default-landing-uri "/login-page"
                 :unauthorized-handler #(throw+ ex/unauthorized-operation)}
                options)]
     (fn [{:keys [session uri request-method] :as req}]
       (if-not (or (re-matches (re-pattern anon) uri)
                   (#{login-uri logout-uri default-landing-uri} uri))
         (case (authorized? req index-uri)
           ::not-login (redirect default-landing-uri)
           ::authorized (let [req (assoc-in req
                                            [:params :current-user]
                                            (get-in session [:current-user]))]
                          (handler req))
           ::un-authorized  (unauthorized-handler))
         (handler req))))))

(defn get-data-level
  "generate search condition map for current user depending on role"
  [curr-user request-method uri]
  (let [auth (some->>  (get-permissions curr-user)
                   (filter (fn [{:keys [method]}] (= method (-> request-method (name) (str/upper-case)))))
                   (filter (fn [e] (re-matches  (re-pattern (:uri e)) uri)))
                   (first))]
    (:scope auth)))

(defn data-level-auth
  "Remove :s-scope from request
   Add :s-scope based on authorities to req params"
  [handler]
  (fn [{:keys [session uri request-method] :as req}]
    (let [curr-user (:current-user session)
          scope (get-data-level curr-user request-method uri)
          req (assoc-in req [:params :s-scope] scope)
          req (assoc-in req [:params :current-user] curr-user)]
      (handler req))))

(defn wrap-base [handler]
  (-> handler
      wrap-context
      wrap-dev
      auth
      data-level-auth
      wrap-formats
      wrap-webjars
      wrap-flash
      (wrap-defaults
       (-> site-defaults
           (assoc-in [:params :keywordize] true)
           (assoc-in [:security :anti-forgery] false)
           (assoc-in [:responses :content-types] true)
           (assoc-in [:session :store] (memory-store session/mem))))))
