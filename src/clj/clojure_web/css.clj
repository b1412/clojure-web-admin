(ns clojure-web.css
  (:require [garden.def :refer [defstyles]]))

(defstyles customize
  [[:.bootstrap-table {:width "1000px"}]
   [:.modal-header {:min-height "16.42857143px"
                    :padding "15px"
                    :border-bottom "1px solid #e5e5e5"
                    :background-color "#428bca"}]

   [:.modal-title {:color "#fff" :display "inline-block"}]
   [:.modal-body [:.container {:width "800px"} [:.bootstrap-table {:width "800px"}]]]
   [:.form-group
    [:.warning {:font-weight "normal"
                :background "#ec971f"
                :color "#DDD"
                :border-radius "2px"
                :padding "1px 4px"
                :margin-top "1px"}]

    [:.error {:font-weight "normal"
              :background "#A94442"
              :color "#DDD"
              :border-radius "2px"
              :padding "1px 4px"
              :margin-top "1px"}]]
   [:.nav-tabs {:margin 0
                :padding 0
                :border 0}]])
