(ns clojure-web.brand
  (:require [clojure-web.components.bs-table
             :refer
             [create-bs-table]]))

(def brand-panel (create-bs-table
                     :entity
                     "brand"))
