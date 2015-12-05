(ns clojure-web.routes.user
  (:require [clojure-web.common.routes-helper :refer [defcrud-routes]]
            [clojure-web.db.entity :refer [user]]
            [clojure-web.render :as render]))

(defcrud-routes user-routes user)



