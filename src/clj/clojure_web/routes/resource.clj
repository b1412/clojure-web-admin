(ns clojure-web.routes.resource
  (:require [clojure-web.common.crud :refer [defcrud-routes]]
            [clojure-web.db.entity :refer [resource]]
            [clojure-web.render :as render]))

(defcrud-routes resource-routes resource)



