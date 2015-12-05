(ns clojure-web.test.common.kit-test
  (:require [clojure-web.common.kit :as kit]
            [midje.sweet :refer [fact facts]]))

(facts "test slice"
  (fact ""
    (kit/slice "abcdef" 2) => "cdef"
    (kit/slice "abcdef" 1 4) => "bcd"
    (kit/slice "abcdef" 0 -1) => "abcde"
    (kit/slice "abcdef" 0 -2) => "abcd"))
