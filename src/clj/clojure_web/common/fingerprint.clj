(ns clojure-web.common.fingerprint
  (:require [clojure-web.db.entity :as e]
            [clojure-web.metadata-kit :refer [has-feature?]]
            [pandect.algo.md5 :refer [md5]]
            [korma.core :as k]
            [taoensso.timbre :as log]
            [slingshot.slingshot :refer [throw+]]))

(def cache (atom {}))

(def expired-ids (atom {}))



(defn calc [entity m]
  (let [columns (->>
                 (e/get-all-columns-with-comment (:table entity))
                 (filter (partial has-feature? :fingerprintable))
                 (map :column-name))]
    (when-not (seq columns)
      (throw+ {:type :not-exist :message "No fingerprintable columns specified in extra-metadata!"}))
    (->> m
         ((apply juxt columns))
         (apply str)
         (md5))))


(defn compare!
  "Compare current entity with entities in cache to decide
   whether create a new record or not"
  [entity m]
  (let [cached-entitis (@cache (:name entity))
        fingerprint (calc entity m)
        id (cached-entitis fingerprint)]
    (if id
      (do (swap! expired-ids
                 assoc
                 (:name entity)
                 (remove #{id} (@expired-ids (:name entity))))
          (assoc m :id id))
      (do
        (swap! cache
               assoc
               (:name entity)
               (assoc (@cache (:name entity)) fingerprint 0))
        m))))

(defn add [entity m]
  (assoc m :fingerprint (calc entity m)))

(defn create [entity m save]
  (let [m (compare! entity (add entity m))]
    (prn (:id m))
    (if (nil? (:id m))
      (save m))))


(defn remove-expired! [entity]
  (let [ids (@expired-ids (:name entity))]
    (->> ids
         (map (fn [id] (-> (k/delete* entity)
                          (k/where {:id id})
                          (k/delete))))
         (doall))))


(defn refresh!
  "Refresh all the fingerprints in db for the specific entity"
  [entity]
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




(defn init!
  "Initialize fingerprints in db for the specific entity"
  [entity]
  (let [entities (k/select entity)]
    (->> entities
         (map :id)
         (swap! expired-ids assoc (:name entity)))
    (->> entities
         (map (juxt :fingerprint :id))
         (into {})
         (swap! cache assoc (:name entity)))))
