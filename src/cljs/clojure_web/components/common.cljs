(ns clojure-web.components.common
  (:require-macros [cljs.core.async.macros :refer [go]]
                   [secretary.core         :refer [defroute]]
                   [reagent.ratom :refer [reaction]])
  (:require [cljs-http.client
             :as http]
            [cljs.core.async
             :refer [<!]]
            [reagent.core
             :as    reagent
             :refer [atom render-component]]
            [re-com.core
             :refer [button v-box modal-panel h-box title border
                     line input-text gap label single-dropdown
                     input-time]]
            [re-com.util
             :refer [get-element-by-id item-for-id]]
            [re-frame.core
             :refer [dispatch subscribe]]
            [superstring.core :as str]
            [clojure-web.metadata-kit :refer [has-feature?]]
            [clojure-web.components.react-bootstrap
             :refer [Button ButtonToolbar Glyphicon Modal
                     ModalBody ModalHeader ModalFooter ModalTitle
                     Grid Row Col]]
            [cljsjs.react-bootstrap]
            [inflections.core :refer  [plural titleize]]
            [clojure.set :as set]))

(defn close [show-flag]
  (fn [e]
    (reset! show-flag false)
    (.preventDefault e)))

(defn make-kw [& args]
  (keyword (apply str args)))

(defn label-name [column-name]
  (-> column-name
      titleize))

(defn renderable?
  "Evaluate if  the given control if value of given metadata is true"
  [metadata-item  restrict-conds]
  (->> restrict-conds
       (map #(metadata-item % 0))
       (not-any? #(= 1 %))))

(defn render-in-conds
  [control metadata & restrict-conds]
  (when (renderable? metadata restrict-conds)
    control))

(defn when-has-feature [control metadata feature]
  (when (metadata feature)
    control))


(defn call-method
  ([entity method options]
   (.bootstrapTable (js/$ (str "#" entity "-table"))
                    method
                    (clj->js options)))
  ([entity method]
   (.bootstrapTable (js/$ (str "#" entity "-table"))
                    method)))

(defn a
  "Displays one a href with i"
  [& {:keys [icon-cls title text  on-click]}]
  [:a {:class icon-cls  :title title :on-click on-click} text])

(defn time->int
  "transform time string to int value.
  e.g. 09:00:00  -> 900"
  [time-val]
  (let [time-val (if (seq time-val) time-val "0000" )
        value (clojure.string/replace time-val ":" "")]
    (if (str/starts-with? value "0")
      (js/parseInt (subs value 1 4))
      (js/parseInt value))))


(defn int->time
  "transform time string to int value.
  e.g. 09:00:00  -> 900"
  [int-val]
  (let [str-val (if (> 1000 int-val) (str "0" int-val) (str int-val))]
    (->> str-val
        ((partial split-at 2))
        (map #(apply str %))
        (interpose ":")
        (apply str )
        (#(str % ":00")))))

(defn get-reserved [column-name metadata]
  (->> metadata
       (filter #(= (name (:column-name %)) (name column-name)))
       (first)
       (#(:reserved % "0"))))

(defn toolbar
  [& {:keys [children]}]
  (fn []
    [h-box
     :gap "15px"
     :children children]))

(defmulti th-value
  (fn [metadata]
    (:type-name metadata)))

(defmethod th-value :default  [metadata]
  (label-name  (:column-name metadata)))

(defmethod th-value "select" [metadata]
  (->> (:column-name metadata)
       (#(subs % 0 (str/last-index-of % "id")))
       (label-name)))


(defn get-remote [args]
  (js->clj
   (.parse js/JSON (aget (.ajax js/$ (clj->js args)) "responseText"))))


(defmulti td-value
  (fn [metadata row]
    (:type-name metadata)))

(defmethod td-value :default [metadata row]
  ((keyword (:column-name metadata)) row))

(defmethod td-value "select" [metadata row]
  (let [column-name (:column-name metadata)
        lookup-table (:lookup-table metadata)
        lookup-table-alise (:lookup-table-alise metadata)
        lookup-label (:lookup-label metadata)]
    (prn row)
    (prn lookup-label)
    ((keyword (str (or lookup-table-alise lookup-table) "." lookup-label)) row)))

(defmethod td-value "enum" [metadata-item row]
  (let [column-name (:column-name metadata-item)
        enum-map (:enum-map metadata-item)
        column-val (->> column-name
                        (keyword)
                        (row)
                        (keyword)
                        (enum-map))]
    column-val))
(defmethod td-value "attachment"
  [metadata row]
  ((keyword (str (:column-name metadata) ".path")) row))


(defn show-on-click
  "Show second component when first component clicked "
  [& {:keys [trigger body title
             on-hide]
      :or {title "Information" }}]
  (let [show? (reagent/atom false)]
    (fn []
      [:div (update trigger 1 assoc :on-click
                    (fn [e]
                      (reset! show? true)
                      (.preventDefault e)))
       [Modal
        {:show @show?
         :on-hide (fn [e]
                    (when on-hide (on-hide))
                    (reset! show? false))
         :bs-size "large"
         :aria-labelledby "contained-modal-title-lg"}
        [ModalHeader
         {:close-button true
          :on-hide (fn [e]
                     (when on-hide (on-hide))
                     (reset! show? false))}
         [ModalTitle title]]
        [ModalBody  (merge body :show? show?)]]])))




(defn show-when [com & data]
  (let [show? (reaction (every?
                         true?
                         (map #((complement nil?) (deref %)) data)))]
    (when @show? com)))

(defn has-permission [permissions permission com]
  (let [show? (seq (filter #(= permission (:key %)) permissions))]
    (when show? com)))

(defn select-to-show
  [& {:keys [trigger body select-fn
             selected-on-hide
             load-row-fn load-data-fn
             event dispatch-fn title
             alert-title alert-message]
      :or {alert-title "Notification"
           alert-message "Please select one record!"
           title "Information"}}]
  (let [selected-show? (reagent/atom false)
        no-selected-show? (reagent/atom false)
        body (merge body :show? selected-show?)
        row (atom {})]
    (fn []
      [:div
       (update trigger 1 assoc :on-click
               (fn [e]
                 (let [selected-row (select-fn)]
                   (if (seq selected-row)
                     (do
                       (reset! row selected-row)
                       (when-not (nil? event)
                         (dispatch-fn row))
                       (reset! selected-show? true)
                       (.preventDefault e))
                     (do
                       (reset! no-selected-show? true)
                       (.preventDefault e))))))
       [Modal
        {:show @selected-show?
         :on-hide (fn [e]
                    (when selected-on-hide (selected-on-hide))
                    (reset! selected-show? false))
         :bs-size "large"
         :aria-labelledby "contained-modal-title-lg"}
        [ModalHeader
         {:close-button true
          :on-hide (fn [e]
                     (when selected-on-hide (selected-on-hide))
                     (reset! selected-show? false))}
         [ModalTitle title]]
        [ModalBody  (if (nil? event)
                      (load-row-fn body row)
                      (let [data (subscribe [event])]
                        (show-when
                         (load-data-fn body row data) data)))]]

       [Modal
        {:show @no-selected-show?
         :on-hide (fn [e]
                    (reset! no-selected-show? false))
         :bs-size "large"
         :aria-labelledby "contained-modal-title-lg"}
        [ModalHeader {:close-button true} [ModalTitle alert-title]]
        [ModalBody  alert-message]
        [ModalFooter
         [Button
          {:on-click (fn [e]
                       (reset! no-selected-show? false)
                       (.preventDefault e))}
          "Ok"]]]])))

(defn FaIcon [name]
  [:i {:class (str "fa fa-" name)}])
