(ns clojure-web.metadata-kit)

(defn has-feature?
  "Test a columns has a feature or not "
  [feature col]
  (let [v (feature col 0)]
    (= 1 v)))
