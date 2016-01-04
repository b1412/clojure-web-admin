(ns clojure-web.enums
  (:require [clojure-web.common.kit :refer [create-symbol]]
            [clojure-web.db.entity :refer [enum]]
            [korma.core :refer [select select*]]))

(defmacro export-enums []
  (let [dics (-> (select* enum)
                 (select)
                 (#(group-by :group %)))]
    `(do ~@(map (fn [[k v]] (let [s# (symbol k)
                                  ms# (create-symbol k "-map")]
                              `(do (def ~ms# ~(->> v
                                                   (map (juxt (comp keyword :label) :value))
                                                   (into {})))
                                   (def ~s# ~(->> v
                                                  (map (juxt (comp keyword :label) :value))
                                                  (into {})
                                                  (keys)
                                                  (set))))))
                dics))))

(export-enums)
