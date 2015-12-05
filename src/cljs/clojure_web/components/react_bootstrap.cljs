(ns clojure-web.components.react-bootstrap
  (:require [cljsjs.react-bootstrap]
            [reagent.core :as reagent]))

(def Button (reagent.core/adapt-react-class (aget js/ReactBootstrap "Button")))
(def ButtonToolbar (reagent.core/adapt-react-class (aget js/ReactBootstrap "ButtonToolbar")))
(def Glyphicon (reagent.core/adapt-react-class (aget js/ReactBootstrap "Glyphicon")))
(def Modal (reagent.core/adapt-react-class (aget js/ReactBootstrap "Modal")))
(def ModalBody (reagent.core/adapt-react-class (aget js/ReactBootstrap "ModalBody")))
(def ModalHeader (reagent.core/adapt-react-class (aget js/ReactBootstrap "ModalHeader")))
(def ModalFooter (reagent.core/adapt-react-class (aget js/ReactBootstrap "ModalFooter")))
(def ModalTitle (reagent.core/adapt-react-class (aget js/ReactBootstrap "ModalTitle")))
(def Table (reagent.core/adapt-react-class (aget js/ReactBootstrap "Table")))
(def Input (reagent.core/adapt-react-class (aget js/ReactBootstrap "Input")))
(def Accordion (reagent.core/adapt-react-class (aget js/ReactBootstrap "Accordion")))
(def Panel (reagent.core/adapt-react-class (aget js/ReactBootstrap "Panel")))
(def Tabs (reagent.core/adapt-react-class (aget js/ReactBootstrap "Tabs")))
(def Tab (reagent.core/adapt-react-class (aget js/ReactBootstrap "Tab")))
(def ProgressBar (reagent.core/adapt-react-class (aget js/ReactBootstrap "ProgressBar")))
(def Nav (reagent.core/adapt-react-class (aget js/ReactBootstrap "Nav")))
(def NavItem (reagent.core/adapt-react-class (aget js/ReactBootstrap "NavItem")))

(def Grid (reagent.core/adapt-react-class (aget js/ReactBootstrap "Grid")))
(def Row (reagent.core/adapt-react-class (aget js/ReactBootstrap "Row")))
(def Col (reagent.core/adapt-react-class (aget js/ReactBootstrap "Col")))

(def TreeView (reagent.core/adapt-react-class js/TreeView))
(def Switch (reagent.core/adapt-react-class js/Switch))










