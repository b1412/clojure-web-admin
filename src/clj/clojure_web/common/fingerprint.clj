(ns clojure-web.common.fingerprint
  (:require [clojure-web.db.entity :as e]
            [clojure-web.metadata-kit :refer [has-feature?]]
            [pandect.algo.md5 :refer [md5]]
            [korma.core :as k]
            [taoensso.timbre :as log]
            [slingshot.slingshot :refer [throw+]]))

(def cache (atom {}))

(defn calc [entity m]
  (let [columns (->>
                 (e/get-all-columns-with-comment (:table entity))
                 (filter (partial has-feature? :fingerprintable))
                 (map :column-name))]
    (->> m
         ((apply juxt columns))
         (apply str)
         (md5))))
(defn refresh! [entity]
  (let [columns (->>
                 (e/get-all-columns-with-comment (:table entity))
                 (filter (partial has-feature? :fingerprintable))
                 (map :column-name))
        entities (->> (k/select entity)
                      (map (fn [e] {:id (:id e)
                                    :fingerprint (calc entity e) })))]
    (->> entities
         (map #(-> (k/update* entity)
                   (k/set-fields {:fingerprint (:fingerprint %)})
                   (k/where {:id (:id %)})
                   (k/update)))
         (doall))))




(defn init [entity]
  (let [column-names (set (e/get-all-column-names (:table entity)))]
    (when-not (contains? column-names :fingerprint)
      (throw+ {:type :not-exist :message "fingerprint column not exists"}))
    (->> (k/select entity)
         (juxt :fingerprint))))
