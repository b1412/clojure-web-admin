(ns clojure-web.uploads
  (:require [reagent.core    :as    reagent]))


(defn bs-file-upload [& {:keys [id attr options events ]
                         :or {id "file"}}]
  (reagent/create-class
   {:component-did-mount
    (fn []
      (let [fi (.fileinput  (js/$ (str "#" id)) (clj->js options))]
        (->> events
            (map (fn [[k f]] (.on fi k f)))
            (doall))))
    :reagent-render
    (fn []
      [:input (merge {:id id} attr)])}))
