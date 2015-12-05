(ns clojure-web.subs
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [re-frame.core :refer [register-sub]]
            [clojure-web.reagent-utils :refer [reg-subs]]))



;; -- Subscription registration  ---------------------------------


(reg-subs :tabs                  ;; usage:  (subscribe [:tabs)
          :menu-tree
          :selected-tab-id
          :permissions
          :role-ress)
