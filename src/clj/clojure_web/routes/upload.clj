(ns clojure-web.routes.upload
  (:require [clojure-web.common.crud :refer [create-entity]]
            [clojure-web.db.entity :refer [upload]]
            [clojure-web.render :as render :refer [*app-context*]]
            [clojure.java.io :as io]
            [compojure.core :refer [context defroutes POST]]
            [taoensso.timbre :as log]))

(defn upload-file [params]
  (let [file (:file params)
        dest (str *app-context* "resources/public/uploads/" (:filename file))
        new-upload {:filename (:filename file)
                    :path (str "/uploads/" (:filename file))}]
    (io/copy (:tempfile file)
             (io/file dest))
    (->> (create-entity upload new-upload)
         (merge {:path (:path new-upload)}))))

(defroutes upload-routes
  (context "/uploads" []
    (POST "/" {params :params} (render/json (upload-file params)))))

