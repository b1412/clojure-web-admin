(ns clojure-web.routes.home
  (:require [clojure-web.db.entity
             :refer
             [alias-fields get-permissions join-table user]]
            [clojure-web.render :as render]
            [compojure.core :refer [defroutes GET POST]]
            [crypto.password.bcrypt :as password]
            [hiccup.core :refer [html]]
            [korma.core :as k]
            [ring.util.response :refer [content-type redirect response]]
            [taoensso.timbre :refer [info]]))

(defn home-page []
  (html
   [:html
    [:head
     (for [item ["/assets/bootstrap/css/bootstrap.css"
                 "/assets/bootstrap-table/dist/bootstrap-table.min.css"
                 "/assets/css/material-design-iconic-font.min.css"
                 "/assets/css/react-bootstrap-switch.css"
                 "/assets/css/material-design-iconic-font.min.css"
                 "/assets/bootstrap-fileinput/css/fileinput.min.css"
                 "/assets/bootstrap-treeview/dist/bootstrap-treeview.min.css"
                 "/assets/css/react-bootstrap-treeview.css"
                 "/assets/css/bootstrap-editable.css"
                 "/assets/css/error-pages.css"
                 "/assets/eonasdan-bootstrap-datetimepicker/build/css/bootstrap-datetimepicker.css"
                 "/assets/bootstrap3-dialog/dist/css/bootstrap-dialog.css"
                 "/assets/css/font-awesome.css"
                 "/assets/css/re-com.css"
                 "/assets/css/customize.css"]]
       [:link {:rel "stylesheet" :href item}])
     (for [item ["/assets/jquery/jquery.min.js"
                 "/assets/bootstrap/js/bootstrap.js"
                 "/assets/react/dist/react-with-addons.js"
                 "/assets/bootstrap-fileinput/js/fileinput.min.js"
                 "/assets/bootstrap-fileinput/js/plugins/canvas-to-blob.min.js"
                 "/assets/js/react-bootstrap-treeview.js"
                 "/assets/js/canvasjs.min.js"
                 "/assets/moment/moment.js"
                 "/assets/js/validator.js"
                 "/assets/js/bootstrap-editable.min.js"
                 "/assets/js/bootstrap-show-password.js"
                 "/assets/js/react-bootstrap-switch.js"
                 "/assets/bootstrap3-dialog/dist/js/bootstrap-dialog.js"
                 "/assets/eonasdan-bootstrap-datetimepicker/build/js/bootstrap-datetimepicker.min.js"
                 "/assets/bootstrap-treeview/dist/bootstrap-treeview.min.js"
                 "/assets/bootstrap-table/dist/bootstrap-table.js"]]
       [:script {:src item}])
     [:title "Welcome to Clojure web admin"]]
    [:body
     [:div {:id "app"} [:div.progress
                        [:div.progress-bar.progress-bar-striped.active
                         {:style "width:100%"} "Loading..."]]]
     (for [item ["/js/out/goog/base.js"
                 "/js/app.js"]]
       [:script {:src item}])
     [:script "goog.require(\"clojure_web.app\")"]]]))

(defn login-page
  []
  (html
    [:head
     (for [item ["/assets/bootstrap/css/bootstrap.css"
                 "/assets/css/login.css"]]
       [:link {:rel "stylesheet" :href item}])
     (for [item ["/assets/jquery/jquery.min.js"
                 "/assets/bootstrap/js/bootstrap.js"
                 "/assets/js/bootstrap-show-password.js"]]
            [:script {:src item}])
     [:title "Welcome to Clojure web admin"]]
   [:body
    [:div.wrapper
     [:form.form-signin {:action "/login" :method "POST"}
      [:h2.form-signin-heading "Please login"]
      [:input.form-control
       {:type "text"
        :name "username"
        :placeholder "Username"}]
      [:input.form-control
       {:type "password"
        :name "password"
        :data-toggle "password"
        :placeholder "Password"}]
      [:button.btn.btn-lg.btn-primary.btn-block
       {:type "submit"}
       "Login"]]]]))


(defn login*
  "user login with username and passwod"
  [username password]
  (if-let [login-user (-> (k/select* user)
                          (alias-fields user)
                          (join-table user)
                          (k/where {:username username})
                          (k/select)
                          (first))]
    (when (password/check password (:password login-user))
      login-user)))

(defn login [session username password]
  (info "username" username "passwod" password)
  (if-let [login-user (login* username password)]
    (let [session (assoc session :current-user login-user)]
      (-> (redirect "/")
          (assoc :session session)))
    (-> (response (str "login failed"))
        (content-type "text/html"))))

(defn logout [req]
  (-> (redirect "/")
      (assoc :session nil)))



(defroutes home-routes
  (GET "/permissions" {params :params}
       (render/json (get-permissions (:current-user params))))
  (GET "/" [] (home-page))
  (GET "/login-page" [] (login-page))

  (POST "/login" {session :session params :params}
    (login session (:username params) (:password params)))
  (GET "/logout" [req] (logout req)))
