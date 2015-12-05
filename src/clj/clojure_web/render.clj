(ns clojure-web.render
  (:require [clj-time.format :as f]
            [clojure.data.json :as json]
            [hiccup.core :refer [html]]
            [ring.util
             [http-response :refer [content-type]]
             [response :refer [response]]])
  (:import [java.sql Date Time Timestamp]
           org.joda.time.DateTime))

(declare ^:dynamic *app-context*)

(defn value-convertor [key value]
  (condp isa? (type value)

    Date
    (f/unparse (f/formatters :date) (DateTime. value))

    Timestamp
    (f/unparse (f/formatters :date-hour-minute-second) (DateTime. value))

    DateTime
    (f/unparse (f/formatters :date-hour-minute-second) value)

    Time
    (str value)
    value))

(defn json [data]
  (-> (response (json/write-str data
                                :value-fn value-convertor))
      (content-type "application/json")))

(defn render404 []
  (html [:head
         [:meta {:charset "UTF-8"}]
         [:title "404"]
         [:meta {:name "msapplication-TileColor", :content "#5bc0de"}]
         [:meta
          {:name "msapplication-TileImage",
           :content "assets/img/metis-tile.png"}]
         [:link
          {:rel "stylesheet",
           :href
           "http://cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/3.3.1/css/bootstrap.min.css"}]
         [:link
          {:rel "stylesheet",
           :href
           "http://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.2.0/css/font-awesome.min.css"}]
         [:link {:rel "stylesheet", :href "assets/css/error-pages.css"}]]
        [:body.error
         [:div.container
          [:div.col-lg-8.col-lg-offset-2.text-center
           [:div.logo [:h1 "404"]]
           [:p.lead.text-muted "Nope, not here."]
           [:div.clearfix]
           [:div.col-lg-6.col-lg-offset-3]
           [:div.clearfix]
           [:br]
           [:div.col-lg-6.col-lg-offset-3
            [:div.btn-group.btn-group-justified
             [:a.btn.btn-warning {:href "/"} "Return home page"]]]]]]))

(defn render500 []
  (html
   [:head
    [:meta {:charset "UTF-8"}]
    [:title "500"]
    [:meta {:name "msapplication-TileColor", :content "#5bc0de"}]
    [:meta
     {:name "msapplication-TileImage",
      :content "assets/img/metis-tile.png"}]
    [:link
     {:rel "stylesheet",
      :href
      "http://cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/3.3.1/css/bootstrap.min.css"}]
    [:link
     {:rel "stylesheet",
      :href
      "http://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.2.0/css/font-awesome.min.css"}]
    [:link {:rel "stylesheet", :href "assets/css/error-pages.css"}]]
   [:body.error
    [:div.container
     [:div.col-lg-8.col-lg-offset-2.text-center
      [:div.logo [:h1 "500"]]
      [:p.lead.text-muted
       "Oops, an error has occurred. Internal server error!"]
      [:div.clearfix]
      [:div.col-lg-6.col-lg-offset-3]
      [:div.clearfix]
      [:br]
      [:div.col-lg-6.col-lg-offset-3
       [:div.btn-group.btn-group-justified
        [:a.btn.btn-warning {:href "/"} "Return home page"]]]]]]))
