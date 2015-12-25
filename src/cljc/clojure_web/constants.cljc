(ns clojure-web.constants
  #?(:cljs (:require-macros [clojure-web.constants :refer [defconst]])))

(defmacro defconst [const-name const-val]
  `(def
     ~(with-meta const-name
        (assoc (meta const-name) :const true))
     ~const-val))

(defconst reserved-columns #{:created-at :updated-at :creator-id :version :deleted})

(defconst sys-params #{:s-scope :current-user :offset :limit :sort :order})

(defconst def-op "=")

(defconst operators #{"=" "like" "and" "or" ">" "<" ">=" "<=" "in" "not-in" "not" "not=" "between"})

(defconst meta-columns #{:column-name :type-name :primary-key :reserved
                         :enum-group :enum-map :nullable :lookup-table
                         :lookup-table-alise :lookup-label :searchable
                         :search-op :hidden-in-grid :hidden-in-form
                         :exportable :importable :column-size :truncatable
                         :decimal-digits :chart-value :chart-label
                         :fingerprintable})
