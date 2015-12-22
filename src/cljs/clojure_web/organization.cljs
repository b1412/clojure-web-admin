(ns clojure-web.organization
  (:require [clojure-web.components.entity-bs-table
             :refer [create-bs-table]]))

(def organization-panel (create-bs-table
                         :entity "organization"))
