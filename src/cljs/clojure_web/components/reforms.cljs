(ns clojure-web.components.reforms
  (:require-macros [cljs.core.async.macros :refer [go]]
                   [reagent.ratom :refer [reaction]]
                   [reagent-forms.macros :refer [render-element]])
  (:require [cljs-http.client :as http]
            [cljs.core.async :refer [<!]]
            [reagent.core  :as    reagent :refer [atom render-component]]
            [reagent-forms.core :refer [bind-fields init-field value-of bind format-type
                                        format-value]]
            [re-frame.core :refer [dispatch subscribe]]
            [superstring.core :as str]
            [clojure-web.components.common :refer [th-value label-name render-in-conds
                                                   show-on-click show-when close
                                                   FaIcon]]
            [clojure-web.components.bs-table :refer [lookup-entity-bs-table]]
            [clojure-web.uploads :refer [bs-file-upload]]
            [clojure-web.components.react-bootstrap :refer [Input
                                                            Button
                                                            ButtonToolbar
                                                            Glyphicon
                                                            Modal
                                                            ModalBody
                                                            ModalHeader
                                                            ModalFooter
                                                            ModalTitle]]

            [bouncer.core :as b]
            [clojure-web.validate :refer [get-validators]]
            [inflections.core :refer  [plural titleize]]))



(defn row [label input]
  [:div.row
    [:div.col-md-2 [:label label]]
   [:div.col-md-5 input]])

(defn input [label type id]
  (row label [:input.form-control {:field type :id id}]))

(defn alert-info [id]
  [:div.row
   [:div.col-md-2]
   [:div.col-md-5
    [:div.alert.alert-danger
     {:field :alert :id (keyword (str "errors." (name id)))}]]])



(defn- set-attrs
  [[type attrs & body] opts & [default-attrs]]
  (into [type (merge default-attrs (bind attrs opts) attrs)] body))

(defmethod init-field :number
  [[_ {:keys [field] :as attrs} :as component] {:keys [doc] :as opts}]
  (render-element attrs doc
    (set-attrs component opts {:type field})))

(defmethod bind :number
  [{:keys [field id fmt]} {:keys [get save! doc]}]
  {:value (let [value (or (get id) "")]
            value)
   :on-change #(save! id (->> % (value-of) (format-type field)))})


(defmethod init-field :datetime
  [[_ {:keys [field] :as attrs} :as component] {:keys [doc] :as opts}]
  (reagent/create-class
   {:component-did-mount
    #(.datetimepicker (js/$ "#datetimepicker1") (clj->js {:viewMode "years"}))
    :reagent-render
    (fn []
      [:div#datetimepicker1.input-group.date
       [:input.form-control {:type "text"}]
       [:span.input-group-addon
        [:span.glyphicon.glyphicon-calendar]]])}))

(defmethod bind :datetime
  [{:keys [field id fmt]} {:keys [get save! doc]}]
  {:value (let [value (or (get id) "")]
            value)
   :on-change #(save! id (->> % (value-of) (format-type field)))})


(defn file-form [& {:keys [column-name form-data show? image]}]
  (when @show?
    [bs-file-upload
     :attr  {:class "file"
             :type "file"
             :multiple "false"
             :name "file"}
     :options {:uploadUrl "/uploads"
               :autoReplace "true"
               :multiple "false"
               :maxFileCount 1
               :overwriteInitial false}
     :events  {"fileuploaded"
              (fn [event data preview-id index]
                (let [resp ((js->clj data) "response")]
                  (swap! form-data assoc column-name (resp "generated-key"))
                  (reset! image (resp "path"))
                  (reset! show? false)))}]))

(defmethod init-field :image
  [[_ {:keys [field id] :as attrs} :as component] {:keys [doc] :as opts}]
  (let [upload-path (reagent/atom
                     ((keyword (str (name id) ".path")) @doc))]
    (fn []
      [:div.form-group
            [:img {:src @upload-path :class "img-responsive"}]
            [show-on-click
             :trigger
             [Button {:bs-style "primary"}
              [FaIcon "upload"] ]
             :body [file-form
                    :image upload-path
                    :column-name id
                    :form-data doc]]])))

(defmethod bind :image
  [{:keys [field id fmt]} {:keys [get save! doc]}]
  {:value (let [value (or (get id) "")]
            value)
   :on-change #(save! id (->> % (value-of) (format-type field)))})


(defmethod init-field :attachment
  [[_ {:keys [field id] :as attrs} :as component] {:keys [doc] :as opts}]
  (let [upload-path (reagent/atom
                     ((keyword (str (name id) ".path")) @doc))]
    (fn []
      [:div.form-group
       (when (not-empty @upload-path)
         [:div
          [:input.form-control {:read-only true :type :text :value  @upload-path}]
          [Button {:bs-style "info"
                   :on-click (fn [e]
                               (aset js/window "location" @upload-path))}
           [FaIcon "download"]]])
       [show-on-click
        :trigger
        [Button {:bs-style "primary"}
         [FaIcon "upload"]]
        :body [file-form
               :image upload-path
               :column-name id
               :form-data doc]]])))

(defmethod bind :attachment
  [{:keys [field id fmt]} {:keys [get save! doc]}]
  {:value (let [value (or (get id) "")]
            value)
   :on-change #(save! id (->> % (value-of) (format-type field)))})


(defmethod init-field :lookup
  [[_ {:keys [field id lookup-table lookup-label] :as attrs} :as component] {:keys [doc] :as opts}]
  (let [label-path (reaction ((keyword (str lookup-table "." (str lookup-label))) @doc))]
    (fn []
      [:div.form-group
       (when (not-empty @label-path)
         [:input.form-control {:read-only true :type :text :value  @label-path}])
       [show-on-click
        :title "Lookup"
        :trigger [Button {:bs-style "primary"}
                  [FaIcon "search-plus"] ]
        :body [lookup-entity-bs-table
               :lookup-table lookup-table
               :column-name id
               :lookup-label lookup-label
               :form-data doc]]])))


(defmethod bind :lookup
  [{:keys [field id fmt]} {:keys [get save! doc]}]
  {:value (let [value (or (get id) "")]
            value)
   :on-change #(save! id (->> % (value-of) (format-type field)))})


(defmulti re-form-input-render
  (fn [metadata form-data]
    (:type-name metadata)))

(defmethod  re-form-input-render :default [metadata-item form-data]
  (let [id (:column-name metadata-item)]
    [:div
     (row (th-value metadata-item)
          [:input.form-control {:field :text
                                :id (keyword id)}])
     (alert-info id)]))

(defmethod  re-form-input-render "decimal" [metadata-item form-data]
  (let [id (keyword (:column-name metadata-item))
        decimal-digits (:decimal-digits metadata-item)]
    [:div
     (row (th-value metadata-item)
          [:input.form-control
           {:field :numeric
            :id id
            :fmt (str "%." decimal-digits "f" )}])
     (alert-info id)]))


(defmethod  re-form-input-render "attachment" [metadata-item form-data]
  (let [id (:column-name metadata-item)
        value (reaction (@form-data id))
        image (atom "")]
    [:div
     (row (th-value metadata-item) [:input.form-control {:field :attachment :id (keyword id)}])
     (alert-info id)]))



(defmethod  re-form-input-render "image" [metadata-item form-data]
  (let [id (:column-name metadata-item)
        value (reaction (@form-data id))
        image (atom "")]
    [:div
     (row (th-value metadata-item) [:input.form-control {:field :image :id (keyword id)}])
     (alert-info id)]))


(defmethod  re-form-input-render "lookup" [metadata-item form-data]
  (let [id (:column-name metadata-item)
        lookup-table (:lookup-table metadata-item)
        lookup-label (:lookup-label metadata-item)]
    [:div
     (row (th-value metadata-item)
          [:input.form-control
           {:field :lookup
            :id (keyword id)
            :lookup-table lookup-table
            :lookup-label lookup-label}])
     (alert-info id)]))


(defmethod  re-form-input-render "password" [metadata-item form-data]
  (let [id (:column-name metadata-item)]
    [:div
     (input (th-value metadata-item) :password (keyword id))
     (alert-info id)]))

(defmethod  re-form-input-render "int" [metadata-item form-data]
  (let [id (:column-name metadata-item)]
    [:div
     (input (th-value metadata-item) :number (keyword id))
     (alert-info id)]))

(defmethod  re-form-input-render "float" [metadata-item form-data]
  (let [id (keyword (:column-name metadata-item))
        decimal-digits (:decimal-digits metadata-item)]
    [:div
     (row (th-value metadata-item) [:input.form-control {:field :numeric :id id}])

     (alert-info id)]))

(defmethod  re-form-input-render "double" [metadata-item form-data]
  (let [id (keyword (:column-name metadata-item))
        decimal-digits (:decimal-digits metadata-item)]
    [:div
     (row (th-value metadata-item) [:input.form-control {:field :numeric :id id}])
     (alert-info id)]))



(defmethod  re-form-input-render "textarea" [metadata-item form-data]
  (let [id (:column-name metadata-item)]
    [:div
     (row (th-value metadata-item) [:textarea.form-control {:field :textarea :id (keyword id)}])
     (alert-info id)]))

(defmethod  re-form-input-render "date" [metadata-item form-data]
  (let [id (:column-name metadata-item)]
    [:div
     (input (th-value metadata-item) :text (keyword id))
     (alert-info id)]))

(defmethod  re-form-input-render "time" [metadata-item form-data]
  (let [id (:column-name metadata-item)]
    [:div
     (input (th-value metadata-item) :text (keyword id))
     (alert-info id)]))

(defmethod  re-form-input-render "datetime" [metadata-item form-data]
  (let [id (:column-name metadata-item)]
    [:div
     (input (th-value metadata-item) :datetime (keyword id))
     (alert-info id)]))


(defmethod  re-form-input-render "select" [metadata-item form-data]
  (let [id (keyword (:column-name metadata-item))
        lookup-table (:lookup-table metadata-item)
        lookup-label (keyword (:lookup-label metadata-item))
        event (keyword (plural lookup-table ))
        data  (subscribe [event])]
    [:div
     (row (th-value metadata-item)
          [:select.form-control {:field :list :id id}
           (for [item @data]
             [:option {:key (:id item)} ( lookup-label item )])])
     (alert-info id)]))

(defmethod  re-form-input-render "enum" [metadata-item form-data]
  (let [id (keyword (:column-name metadata-item))
        data (->> (:enum-map metadata-item)
                  (map (fn [[k v]] [(name k)  v])))]
    [:div
     (row (th-value metadata-item)
          [:select.form-control {:field :list :id id}
           (for [[k v] data]
             [:option {:key k} v])])
     (alert-info id)]))


(defn get-defs
  "Get all default values from metadata"
  [metadata]
  (->> metadata
       (remove (fn [{:keys [column-def]}] (nil? column-def)))
       (map  (juxt (comp keyword :column-name) :column-def))
       (into {})))

(defn form
  [& {:keys [entity metadata form-data show? submit-fn]}]
  (when (empty? @form-data)
    (reset! form-data (get-defs @metadata)))
  (let [validators (get-validators @metadata)
        process-ok (fn [data]
                     (submit-fn data)
                     (reset! show? false))
        form-template (->> @metadata
                           (map-indexed
                            (fn [idx itm]
                              (render-in-conds
                               ^{:key idx}
                               (re-form-input-render itm form-data)
                               itm
                               :hidden-in-form
                               :reserved
                               :primary-key)))
                           (conj [:div])
                           (vec))
        datas (atom '())]
    (->> @metadata
         (filter #(= "select" (:type-name %)))
         (map (fn [metadata-item]
                (let [lookup-table (:lookup-table metadata-item)
                      event (keyword (plural lookup-table ))]
                  (swap! datas conj (subscribe [event]))
                  (dispatch [event]))))
         (doall))

    (fn []
      [:div
       (apply (partial show-when [bind-fields form-template form-data])
              @datas)
;;              [:label (str @form-data)]
       [ButtonToolbar
        [Button
         {:bs-style "primary"
          :on-click
          (fn []
            (let [result (apply (partial b/validate @form-data) validators)
                  errs (get result 0)]
              (if errs
                (->> errs
                     (map (fn [[k v]]
                            (swap! form-data assoc-in [:errors k] (first v))))
                     (doall))
                (process-ok form-data))))}
         "Save"]
        [Button
         {:on-click (close show?)}
         "Cancel"]]])))
