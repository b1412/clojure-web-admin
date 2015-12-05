(ns clojure-web.common.crud
  (:require [bouncer.core :as b]
            [clj-time
             [format :as f]
             [local :as l]]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure-web.common
             [excel :as excel]
             [kit :refer [create-kw dash->underscore subs-before underscore->dash]]]
            [clojure-web
             [metadata-kit :refer [has-feature?]]
             [constants :refer [def-op reserved-columns sys-params meta-columns]]
             [exceptions :as ex]
             [render :as render]
             [validate :refer [get-validators]]]
            [clojure-web.db.entity :as e]
            [compojure.api.sweet
             :refer
             [context* defroutes* DELETE* GET* POST* PUT*]]
            [crypto.password.bcrypt :as password]
            [dk.ative.docjure.spreadsheet :refer [load-workbook select-sheet select-columns]]
            [korma.core :as k]
            [ring.util.http-response :refer [file-response header ok]]
            [slingshot.slingshot :refer [throw+]]
            [taoensso.timbre :refer [debug info]]))

(defn current-time
  "Get current time in format (1111-11-11T11:11:11)"
  []
  (f/unparse (f/formatters :date-hour-minute-second) (l/local-now)))

(defn- ref-col->op-map [ref-table]
  (->>
   (e/get-all-columns-with-comment ref-table)
   (map (juxt #(create-kw ref-table "." (name (:column-name %)))
              #(:search-op % "=")))
   (into {})))

(defn col->op-map [table]
  (let [ref-tables (e/get-ref-entity (@e/table->entity table))
        m (->>
           (e/get-all-columns-with-comment table)
           (map (juxt :column-name #(:search-op % "=")))
           (into {}))
        ref-m  (->> ref-tables
                    (map ref-col->op-map)
                    (reduce merge))]
    (merge m ref-m)))

(defn contains-column? [table column]
  (->> (:table table)
       (e/get-column-names)
       (#((set %) column))))

(defn remove-unknown-columns [table columns]
  (let [known-columns (set (e/get-column-names (:name table)))]
    (->> columns
         (filter (fn [[k v]] (known-columns k)))
         (remove (fn [[k v]] (reserved-columns (name k))))
         (into {}))))

(defn- append-col-if-exist [entity col val params]
  (if (contains-column? entity col)
    (assoc params col val)
    params))

(defn append-cols-if-exist
  [entity m params]
  (reduce-kv (fn [params col val]
               (append-col-if-exist entity col val params))
             params
             m))

(defn data-level-access [entity scope id curr-user]
  (let [ent (-> (k/select* entity)
                (e/alias-fields entity)
                (e/join-table entity)
                (k/where {:id id})
                (k/select)
                (first))]
    (if scope
      (condp = scope
        "system" true
        "org"    (if (not=
                      (:organization-id curr-user)
                      (:user.organization-id ent))
                   (throw+ {:type ex/unauthorized-operation :message "unauthorized operation"}))
        "user"   (if (not= (:id curr-user) (:creator-id ent))
                   (throw+ {:type ex/unauthorized-operation :message "unauthorized operation"}))
        (throw+ {:type ex/unknown :message (str "unknown scope: " scope )})))))

(defn data-level-cond [scope curr-user]
  (case scope
    "system" {}
    "orgs" {}
    "org"  {:user.organization-id (:organization.id curr-user)}
    "user" {:creator-id (:id curr-user)}
    (throw+ {:type ex/unknown :message (str "unknown scope: " scope )})))

(defn data-level-read [scope current-user params]
  (if scope
    (merge (data-level-cond scope current-user) params)
    params))

(defmulti op-val-adapter
  (fn [[k [op _]]]
    (name op)))


(defmethod op-val-adapter :default [kv] kv)

(defmethod op-val-adapter "left-like" [[k [op v]]]
  [k [op (str "%" v )]])

(defmethod op-val-adapter "right-like" [[k [op v]]]
  [k [op (str  v "%")]])

(defmethod op-val-adapter "like" [[k [op v]]]
  [k [op (str "%" v "%")]])


(defn query-params [params]
  (debug "pre cond" params)
  (let [grouped-params (group-by #(.endsWith (name (key %)) "-op") params)
        params (into {} (grouped-params false))
        table (:entity params)
        metadata (merge (col->op-map table)
                        (->> (grouped-params true)
                             (map (fn [[k v]] [(-> k
                                                   (name)
                                                   (subs-before "-op")
                                                   (keyword)) v]))
                             (into {})))
        params (dissoc params :entity)
        scope (:s-scope params)
        current-user (:current-user params)
        params (->> params
                    (remove #(sys-params (key %)))
                    (data-level-read scope current-user)
                    (map (fn [[k v]] {k [(symbol (k metadata def-op) )  v]}))
                    (into {})
                    (map op-val-adapter)
                    (into {})
                    (map (fn [[k v]] {(dash->underscore k) v}))
                    (into {}))]
    (debug "post cond" params)
    params))


(defmulti value-out-adapter
  (fn [entity [k v]]
    (let [metadata (e/get-col-metadata entity k)]
      (keyword (:type-name metadata)))))

(defmethod value-out-adapter :default [entity [k v]]
  [k v])

(defmethod value-out-adapter :image [entity [k v]]
  (let [u (first (k/select e/upload (k/where {:id v})))]
    {k v
     (keyword (str (name k) ".path")) (:path u)}))

(defmethod value-out-adapter :attachment [entity [k v]]
  (let [u (first (k/select e/upload (k/where {:id v})))]
    {k v
     (keyword (str (name k) ".path")) (:path u)}))

(defn query-entity
  [entity conds]
  (debug "query " entity " " conds)
  (let [base-query (-> (k/select* entity)
                       (e/join-table entity)
                       (k/where (query-params conds)))
        cnt  (-> base-query
                 (k/aggregate (count :*) :cnt)
                 (k/select)
                 (first)
                 (:cnt))
        result (-> base-query
                   (e/alias-fields entity)
                   (k/order ((comp dash->underscore keyword #(:sort  % :id)) conds)
                            ((comp keyword #(:order % :DESC)) conds))
                   (k/offset (:offset conds  0))
                   (k/limit (:limit conds 10))
                   (k/select))
        result (->> result
                    (pmap #(->> %
                                (pmap (partial value-out-adapter (:name entity)))
                                (into {}))))]
    {:rows result :total cnt}))

(defmulti value-in-adapter
  (fn [entity [k v]]
    (let [metadata (e/get-col-metadata entity k)]
      (keyword (:type-name metadata)))))

(defmethod value-in-adapter :default [entity [k v]]
  [k v])

(defmethod value-in-adapter :password [entity [k v]]
  [k (password/encrypt v)])


(defn create-entity [entity params]
  (debug  params)
  (let [metadatas (e/get-all-columns-with-comment (:name entity))
        validators (get-validators metadatas)
        result (apply (partial b/validate params) validators)
        errs (get result 0)
        s-scope (:s-scope params)
        current-user (:current-user params)
        params (->> params
                    (remove #(sys-params (key %)))
                    (into {})
                    (append-cols-if-exist
                     entity
                     {:creator-id (:id current-user)
                      :created-at (current-time)
                      :updated-at (current-time)})
                    (map (partial value-in-adapter (:name entity)))
                    (into {}))]

    (if errs
        (throw+ {:type ex/illegal-argument
                 :message (->> errs
                               (map (fn [[_ v]] (first v)))
                               (str/join ". "))}))
    (-> (k/insert* entity)
        (k/values params)
        (k/insert))))

(defn get-entity [entity id params]
  (debug "get" (:name entity) id)
  (debug "pre" params)
  (let [scope (:s-scope params)
        curr-user (:current-user params)
        params (->> (assoc params :id id)
                    (remove #(sys-params (key %)))
                    (into {})
                    (map (fn [[k v]] {(dash->underscore k) v}))
                    (into {}))]
    (debug "post" params)

    (if (= "-1" id)
      (->> (e/get-column-names (:name entity))
           (map (fn [e] {(keyword (underscore->dash e)) ""}))
           (into {}))
      (do
        (data-level-access entity scope id curr-user)
        (-> (k/select* entity)
            (k/where params)
            (e/alias-fields entity)
            (e/join-table entity)
            (k/select)
            (first))))))

(defn optimistic-lock
  "Compare update-time of the current entity with update-time in DB"
  [id entity update-time]
  (if (contains-column? entity :update-time)
    (let [time-in-db (-> (k/select* entity)
                         (k/where {:id id})
                         (k/select)
                         (first)
                         (:update-time)
                         (#(f/unparse (f/formatters :date-hour-minute-second) %)))]
      (if (not= time-in-db update-time)
        (throw+ {:type ::already-updated
                 :message "The record you updating already updated by others,
                           please refresh it first"})))))

(defn update-entity [entity id params]
  (debug "pre" params)
  (optimistic-lock  id entity (:updated-at params))
  (let [scope (:s-scope params)
        curr-user (:current-user params)
        metadatas (e/get-all-columns-with-comment (:name entity))
        validators (get-validators metadatas)
        result (apply (partial b/validate params) validators)
        errs (get result 0)
        params (->> params
                    (remove-unknown-columns entity)
                    (into {})
                    (append-cols-if-exist entity {:updated-at (current-time)})
                    (map (partial value-in-adapter (:name entity)))
                    (into {}))]
    (debug "post"  params)
    (data-level-access entity scope id curr-user)
    (-> (k/update* entity)
        (k/set-fields params)
        (k/where {:id id})
        (k/update))))

(defn delete-entity [entity id params]
  (let [scope (:s-scope params)
        curr-user (:current-user params)
        params (->> params
                    (remove-unknown-columns entity)
                    (into {})
                    (append-cols-if-exist entity {:update-time (current-time)}))]
    (data-level-access entity scope id curr-user)
    (-> (k/delete* entity)
        (e/join-table entity)
        (k/where {:id id})
        (k/delete))))

(defn render-file [entity]
  (excel/export-file entity)
  (-> (file-response (str (java.io.File. (str "/tmp/" (:table entity) ".xlsx"))))
      (header "content-disposition" (str "attachment; filename=" (:table entity) ".xlsx"))))

(defn render-excel-template  [entity]
  (excel/export-excel-template entity)
  (-> (file-response (str (java.io.File. (str "/tmp/" (:table entity) "-template.xlsx"))))
      (header "content-disposition" (str "attachment; filename=" (:table entity) "-template.xlsx"))))


(defn process-excel [entity params]
  (let [columns (->> (e/get-all-columns-with-comment (:table entity))
                     (filter (partial has-feature? :importable))
                     (mapv :column-name))
        file (:file params)
        dest (str render/*app-context* "resources/public/uploads/" (:filename file))]
    (io/copy (:tempfile file)
             (io/file dest))
    (let [columns (zipmap
                   (map (comp keyword str char) (range 65 91))
                   columns)
          entities (->> (load-workbook dest)
                        (select-sheet "Entity")
                        (select-columns
                         columns)
                        (rest))]
      (-> (k/insert* entity)
          (k/values entities)
          (k/insert)))))



(defmacro defcrud-routes [routes entity# & rest]
  (let [id (str "id" (gensym))
        sid (symbol id)]
    `(defroutes* ~routes
       (context* ~(str "/" entity# "s") []
                 :tags [~(str entity# "s")]
                 ~@rest
                 (GET* "/"  {params# :params} (render/json
                                               (query-entity ~entity#
                                                             (assoc params#
                                                                    :entity
                                                                    (:name ~entity#)))))

                 (GET* "/meta" [] (ok (e/get-all-columns-with-comment (:name ~entity#))))

                 (GET* "/excel/template" [] (render-excel-template ~entity#))

                 (GET* "/excel" [] (render-file ~entity#))



                 (POST* "/excel" {params# :params}
                        (render/json (process-excel ~entity# params#)))

                 (POST* "/"  {params# :params} (render/json (create-entity
                                           ~entity#
                                           params#)))
                 (context* ~(str "/:" id)  [~sid]
                           (GET*  "/" {params# :params}
                                  (render/json (get-entity
                                                ~entity#
                                                ~sid
                                                (dissoc params# (keyword ~id)))))

                           (PUT*    "/" {params# :params}
                                    (render/json (update-entity
                                                  ~entity#
                                                  ~sid
                                                  (dissoc params# (keyword ~id)))))

                           (DELETE* "/" {params# :params}
                                    (render/json (delete-entity
                                                  ~entity#
                                                  ~sid
                                                  (dissoc params# (keyword ~id))))))))))
