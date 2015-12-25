(ns clojure-web.components.search-box
  (:require
   [clojure-web.components.react-bootstrap
    :refer
    [Button ButtonToolbar Glyphicon Modal
     ModalBody ModalHeader ModalFooter ModalTitle
     Grid Row Col]]
   [clojure-web.components.common
    :refer [td-value th-value call-method]]
   [reagent.core
    :as reagent ]
   [reagent-forms.core
    :refer
    [bind-fields]]))



(defn search-box
  [& {:keys [entity metadatas params]}]
  (let [params (atom {})
        on-click (fn [event]
                   (let [data (->>
                               @params
                               (filter (fn [[k v]] (not-empty (str v))))
                               (into {}))])
                   false)
        inputs (->> metadatas
                    (filter #(= 1 (% :searchable)))
                    (map-indexed (fn [idx metadata]
                                   [[Col {:md 1 } (th-value metadata) ":"]
                                    [Col {:md 2}
                                     [:input.form-control
                                      {:on-change #(swap! params
                                                         assoc
                                                         (keyword (:column-name metadata))
                                                         (.-target.value %))
                                       :name (keyword (:column-name metadata))
                                       :type "text"
                                       :id (keyword (:column-name metadata))
                                       :placeholder (th-value metadata)}]]]))
                    (mapcat identity)
                    (take 12))
        inputs (->> inputs (#(partition
                               6
                               6
                               [[Col {:md 1 :md-offset (- 7 (rem (count inputs) 6))}
                                 [Button {:on-click
                                          (fn []
                                            (call-method entity "refresh" {:query @params}))}
                                  [Glyphicon {:glyph "search"}] "Search"]]] %))
                    (map (fn [rows]
                           (->> rows
                                (concat [Row {:class-name "show-grid"}])
                                (vec))))
                    (concat [Grid])
                    (vec))]

    (fn []
      [:div.container.search-box inputs])))
