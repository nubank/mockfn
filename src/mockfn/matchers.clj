(ns mockfn.matchers
  (:import (clojure.lang IFn)))

(defprotocol Matcher
  (matches? [this actual])
  (description [this]))

(defrecord Exactly [expected]
  Matcher
  (matches? [this actual] (= expected actual))
  (description [this] (format "exactly %s" expected)))

(def exactly ->Exactly)
