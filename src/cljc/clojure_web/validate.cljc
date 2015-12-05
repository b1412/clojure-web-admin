(ns clojure-web.validate
  (:require [bouncer.core :as b]
            [bouncer.validators :as vv]))

(defn get-validators [metadata]
  (->> metadata
       (filter #(= 0 (:nullable %)))
       (filter #(not= "id" (name (:column-name %))))
       (map  #(hash-map (keyword (:column-name %))
                         vv/required))
       (reduce merge)
       (vec)
       (flatten)))
