(ns clojure-web.user
  (:require [clojure-web.components.entity-bs-table
             :refer [create-bs-table]]))


(def user-panel (create-bs-table
                 :entity "user" ))
