(ns clojure-web.routes.computer
  (:require [clojure-web.common.routes-helper :refer [defcrud-routes]]
            [clojure-web.db.entity :refer [computer]]
            [clojure-web.render :as render]))

(defcrud-routes computer-routes computer)



