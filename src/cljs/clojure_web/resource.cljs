(ns clojure-web.resource
  (:require [clojure-web.components.bs-table :refer [create-bs-table]]))


(def resource-panel (create-bs-table
                     :entity
                     "resource" ))
