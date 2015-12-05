(ns clojure-web.session)

(defonce mem (atom {}))

(defn- current-time []
  (quot (System/currentTimeMillis) 1000))

(defn- expired? [[id session]]
  (pos? (- (:ring.middleware.session-timeout/idle-timeout session) (current-time))))

(defn clear-expired-sessions []
  (clojure.core/swap! mem #(->> % (filter expired?) (into {}))))

