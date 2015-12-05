(ns clojure-web.routes.brand
  (:require [clojure-web.common.crud :refer [defcrud-routes]]
            [clojure-web.db.entity :refer [brand]]
            [clojure-web.render :as render]))

(defcrud-routes brand-routes brand)



