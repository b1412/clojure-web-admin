(ns clojure-web.common.kit)

(defmacro defconst [const-name const-val]
  `(def
     ~(with-meta const-name
        (assoc (meta const-name) :const true))
     ~const-val))

(defn dissoc-keywords [m]
  (let [ks (->> m (keys) (filter keyword?))]
    (prn ks)
    (reduce (fn [m k] (dissoc m k)) m  ks)))

(defn remove-empty-val [m]
  (remove (fn [[k v]] (empty? (str v))) m))

(defn underscore->dash [k]
  (if (keyword? k)
    (let [kn (name k) idx (.indexOf kn "_")]
      (if-not (= -1 idx)
        (keyword (str (subs kn 0 idx) "-" (subs kn (inc idx)))) k))
    (let [idx (.indexOf k "_")]
      (if-not (= -1 idx)
        (str (subs k 0 idx) "-" (subs k (inc idx))) k))))

(defn dash->underscore [k]
  (if (keyword? k)
    (let [kn (name k) idx (.indexOf kn "-")]
      (if-not (= -1 idx)
        (keyword (str (subs kn 0 idx) "_" (subs kn (inc idx)))) k))
    (let [idx (.indexOf k "-")]
      (if-not (= -1 idx)
        (str (subs k 0 idx) "_" (subs k (inc idx))) k))))

(defn column-adapter [m]
  (->> m
       (map (fn [[k v]] [(underscore->dash k) v]))
       (into {})))

(defn column-adapter2 [m]
  (->> m
       (map (fn [[k v]] {(dash->underscore k) v}))
       (into {})))

(defn trans-column-key [m]
  (->> m
       (map (fn [[k v]] {(-> k (name) (keyword)) v}))
       (into {})))

(def classpathes (seq (.getURLs (java.lang.ClassLoader/getSystemClassLoader))))

(defn mapm [f coll]
  (->> coll
       (map f)
       (into {})))

(defn update-by [pred f col]
  (map #(if (pred %) (f %) %) col))

(defn updatev-by [pred f col]
  (mapv #(if (pred %) (f %) %) col))

(defn create-symbol [& args]
  (symbol (apply str args)))

(defn create-kw [& args]
  (keyword (apply str args)))

(defn subs-before [src bef]
  (let [index (.indexOf src bef)]
    (subs src 0 index)))

(defn subs-after [src aft]
  (let [index (.indexOf src aft)]
    (subs src (+ index (count aft)))))

(defn subs-between [src bef aft]
  (let [start (+ (.indexOf src bef) (count bef))
        end (.indexOf src aft)]
    (prn start end)
    (subs src start end)))

(defn transform-map [m {:keys [k-fn v-fn]}]
  (into {} (for [[k v] m] [(if (nil? k-fn)  k (k-fn k))
                           (if (nil? v-fn)  v (v-fn v))])))

(defn slice
  "Like subs, but support negative index"
  ([s start]
   (subs s start))
  ([s start end]
   (if (neg? end)
     (let [end (+ end (count s))]
       (subs s start end))
     (subs s start end))))
