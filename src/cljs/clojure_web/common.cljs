(ns clojure-web.common
  (:require
   [clojure.string :as str]))

(defn ->js [var-name]
      (-> var-name
          (str/replace #"/" ".")
          (str/replace #"-" "_")))
