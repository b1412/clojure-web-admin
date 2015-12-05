(ns clojure-web.metadata-kit)

(defn has-feature?
  "Test a columns has a feature or not "
  ([feature col ]
   (has-feature? col feature 1))
  ([feature col  true-val]
   (let [v (feature col 0)]
     (= true-val v))))
