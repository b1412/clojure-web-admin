(ns clojure-web.routes.organization
  (:require [clojure-web.common.routes-helper :refer [defcrud-routes]]
            [clojure-web.db.entity :refer [organization]]
            [clojure-web.render :as render]))

(defcrud-routes organization-routes organization)



