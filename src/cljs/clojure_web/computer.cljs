(ns clojure-web.computer
  (:require [clojure-web.components.entity-bs-table
             :refer
             [create-bs-table]]))

(def computer-panel (create-bs-table
                     :entity
                     "computer"))
