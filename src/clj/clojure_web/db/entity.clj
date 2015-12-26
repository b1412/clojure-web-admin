(ns clojure-web.db.entity
  (:require [clojure.core.cache :as cache]
            [clojure.java.jdbc :as jdbc]
            [clojure.walk :as walk]
            [clojure-web.common.kit :as kit]
            [clojure-web.constants :refer [meta-columns]]
            [environ.core :refer [env]]
            [korma
             [core :as k]
             [db :refer [defdb mysql]]]
            [superstring.core :as str]
            [taoensso.timbre :as log])
  (:refer-clojure :exclude [group])
  (:import [com.alibaba.druid.filter Filter]
           [com.alibaba.druid.pool DruidDataSource]))

(alter-var-root
 (var k/where*)
 (fn [f]
   #(f %1
       (walk/prewalk
        (fn [e]
          (if (:korma.sql.utils/generated e)
            (update e :korma.sql.utils/generated
                    (fn [k] (str/replace k "-" "_")))
            e)) %2))))



(def mysql-db {:subprotocol "mysql"
               :subname "//127.0.0.1:3306/clojure_web"
               :user "root"
               :driver-class "com.mysql.jdbc.Driver"
               :password "root"
               :init-pool-size 4
               :max-pool-size 20})

(defn pooled-datasource [db-spec]
  (let [{:keys [classname subprotocol subname user password driver-class
                initial-size min-idle max-active max-wait
                time-between-connect-error-millis
                time-between-eviction-runs-millis
                min-evictable-idle-time-millis
                validation-query
                test-while-idle
                test-on-borrow
                test-on-return
                remove-abandoned
                remove-abandoned-timeout-millis
                log-abandoned
                 max-pool-prepared-statement-per-connection-size]}
        (merge {:initial-size 10
                :min-idle 10
                :max-active 100
                :max-wait DruidDataSource/DEFAULT_MAX_WAIT
                :time-between-evictionRunsMillis
                DruidDataSource/DEFAULT_TIME_BETWEEN_EVICTION_RUNS_MILLIS
                :minEvictableIdleTimeMillis
                DruidDataSource/DEFAULT_MIN_EVICTABLE_IDLE_TIME_MILLIS
                :time-between-connectErrorMillis
                DruidDataSource/DEFAULT_TIME_BETWEEN_CONNECT_ERROR_MILLIS
                :validation-query  "select 1"
                :test-while-idle   true
                :test-on-borrow   false
                :test-on-return  false
                :remove-abandoned  false
                :remove-abandoned-timeout-millis  (* 300  1000)
                :log-abandoned  false
                :max-pool-prepared-statement-perConnectionSize -1}
               db-spec)
        cpds (doto (DruidDataSource.)
               (.setUrl (str "jdbc:" subprotocol ":" subname))
               (.setUsername user)
               (.setPassword password)
               (.setDriverClassName driver-class)
               (.setInitialSize initial-size)
               (.setMinIdle min-idle)
               (.setMaxActive　max-active)
               (.setMaxWait max-wait)

;;;               (.setTimeBetweenConnectErrorMillis time-between-connect-error-millis)
;;;               (.setTimeBetweenEvictionRunsMillis time-between-eviction-runs-millis)
;;;               (.setMinEvictableIdleTimeMillis min-evictable-idle-time-millis)
;;;               (.setValidationQuery validation-query)
;;;               (.setTestWhileIdle　test-while-idle)
;;;               (.setTestOnBorrow test-on-borrow)
;;;               (.setTestOnReturn test-on-return)
;;;               (.setRemoveAbandoned remove-abandoned)
;;;               (.setRemoveAbandonedTimeoutMillis remove-abandoned-timeout-millis)
;;;               (.setLogAbandoned log-abandoned)
;;;               (.setMaxPoolPreparedStatementPerConnectionSize max-pool-prepared-statement-per-connection-size)
               )]
               {:datasource cpds}))

(def pooled-mysql-db (pooled-datasource  mysql-db))

(defdb db (mysql {:db "clojure_web"
                  :user "root"
                  :password "root"}))



(def entities
  (jdbc/with-db-metadata [md mysql-db]
    (jdbc/metadata-result (.getTables md nil nil "" (into-array ["TABLE" "VIEW"])))))

(def tables (map :table_name (jdbc/with-db-metadata [md mysql-db]
                               (jdbc/metadata-result (.getTables md nil nil nil (into-array ["TABLE" "VIEW"]))))))

(defn get-pk [table]
  (:column_name
   (first
    (jdbc/with-db-metadata [md mysql-db]
      (jdbc/metadata-result (.getPrimaryKeys md nil nil table))))))


(def table->entity (atom {}))

(defmacro defent [table & body]
  `(do (declare ~table)
       (k/defentity ~table
         (k/transform kit/column-adapter)
         (k/prepare kit/column-adapter2)
         (k/table (-> (:name (meta (var ~table)))
                      (str/replace #"-" "_")
                      (keyword)))
         ~@body)
       (swap! table->entity assoc (:table ~table) ~table)))

(declare user)

(defent resource
  (k/belongs-to resource {:fk :parent_id}))

(defent role)

(defent role-resource)

(defent organization
  (k/belongs-to organization {:fk :parent_id}))

(defent user
  (k/belongs-to role)
  (k/belongs-to organization))

(defent dictionary)

(defent upload)

(defent brand
  (k/belongs-to user {:fk :creator_id}))

(defent computer
  (k/belongs-to brand)
  (k/belongs-to user {:fk :creator_id}))



(defn get-children [root coll]
  (filter #(= (:parent_id %) (:id root)) coll))

(defn menu-tree [root coll]
  (let [children (get-children root coll)]
    {:text (:label root)
     :id (:key root)
     :nodes (->> children
                 (mapv (fn [child]
                         (if (seq (get-children child coll))
                           (merge {:selectable false
                                   :expanded false}
                                  (menu-tree  child coll))
                           (hash-map
                            :id (:key child)
                            :selectable true
                            :text (:label child))))))}))

(defn get-all-columns [table]
  (->> (jdbc/with-db-metadata [md mysql-db]
         (jdbc/metadata-result (.getColumns md nil nil nil nil)))
       (filter #(= table (:table_name %)))
       (map (comp #(update % :type-name (comp keyword str/lower-case))
                  #(update % :column-name (comp  keyword kit/underscore->dash))
                  #(kit/transform-map % {:k-fn kit/underscore->dash})))))


(defn parse-int
  "Parse to int if string,if not return original value"
  [s]
  (try (Integer/parseInt s)
       (catch Exception e s)))

(def metadata-mem  (atom {}))

(defn get-all-columns-with-comment [table]
  (or
   (@metadata-mem table)
   (let [columns
         (->>
          (get-all-columns table)
          (map (comp
                (fn [m]
                  (if (= "YES" (:is-autoincrement m))
                    (assoc m :is-autoincrement 1)
                    (assoc m :is-autoincrement 0)))
                (fn [m]
                  (merge m
                         (let [col-name (:type-name m)
                               enum-group (:enum-group m)]
                           (if (= "enum" col-name)
                             (-> (k/select* dictionary)
                                 (k/where {:group enum-group})
                                 (k/select)
                                 (#(map (juxt :value :label) %))
                                 (#(into {} %))
                                 ((partial hash-map :enum-map)))))))
                (fn [m]
                  (merge m
                         (when-let [remark (:remarks m)]
                           (->> remark
                                (str/split-lines)
                                (map (fn [args] (str/split args #"=")))
                                (map (fn [[k v]] {(keyword k)
                                                  (-> v (parse-int))}))
                                (into {})))))))

          (map (fn [m] (->> m
                            (map (fn [[k v]] {(keyword k) v}))
                            (into {})
                            (filter (fn [[k v]] (meta-columns k)))
                            (into {}))))
          (map (fn [m] (if (= (name (:column-name m)) (get-pk table))
                         (assoc m :primary-key 1)
                         m))))
         results (apply list columns)]
     (swap! metadata-mem assoc table results)
     results)))

(defn  get-col-metadata [entity col]
  (->> (get-all-columns-with-comment entity)
       (filter #(= col (:column-name %)))
       (first)))

(defn get-column-names [table]
  (->> (get-all-columns table)
       (map :column-name)))

(defn get-all-column-names [entity]
  (->> (get-all-columns entity)
       (map (comp kit/dash->underscore :column-name))))


(defn get-ref-entity [entity]
  (let [rels (:rel entity)]
    (->> rels
         (map (comp force val))
         (filter #(= :belongs-to (:rel-type %)))
         (map (juxt
               :table
               (comp #(str/replace % "`" "") :korma.sql.utils/generated :pk)
               (comp #(str/replace % "`" "") :korma.sql.utils/generated :fk)))
         (map (fn [[t pk fk]] (let [col (-> fk
                                            (kit/subs-after ".")
                                            (kit/underscore->dash)
                                            (keyword))
                                    metadata (get-col-metadata (:table entity) col)
                                    a (:lookup-table-alise metadata t)]
                                [t a (str a "." (kit/subs-after pk ".")) fk]))))))

(defn get-field-alias
  [entity]
  (let [ref-entities (get-ref-entity entity)]
    (->> ref-entities
         (map (fn [[table a _ _]] [a (get-all-column-names table)]))
         (mapcat (fn [[a columns]]
                   (->> columns
                        (map (fn [col]
                               (let [n (kit/create-kw (name a) "." (name col))]
                                 [n n])))))))))

(defn alias-fields
  "give all sub field in ref tables a alias"
  [env entity]
  (apply k/fields (concat [env]
                          (get-field-alias entity)
                          (get-all-column-names (:table entity)))))

(defn join-table
  [env entity]
  (->> (get-ref-entity entity)
       (reduce (fn [env [t a pk fk]]
                 (let [pk (kit/create-kw pk)
                       fk (kit/create-kw fk)]
                   (k/join env [t a] (= pk fk))))
               env)))

(defn get-permissions [user]
  (-> (k/exec-raw ["select res.id, `key`, label, uri, method, scope, `type`,  parent_id
                      from resource res
                      join role_resource rel
                      on (res.id = rel.resource_id)
                      where rel.role_id = ?" [(:role.id user)]] :results)
      (set)))
