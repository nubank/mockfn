(ns mockfn.matchers
  (:require [mockfn.internal.utils :as utils]))

(defprotocol Matcher
  (matches? [this actual])
  (description [this]))

(defrecord Exactly [expected]
  Matcher
  (matches? [this actual] (= expected actual))
  (description [this]
    (utils/formatted "exactly %s times" expected)))

(def exactly ->Exactly)

(defrecord AtLeast [expected]
  Matcher
  (matches? [this actual] (>= actual expected))
  (description [this] (utils/formatted "at least %s times" expected)))

(def at-least ->AtLeast)

(defrecord AtMost [expected]
  Matcher
  (matches? [this actual] (<= actual expected))
  (description [this] (utils/formatted "at most %s times" expected)))

(def at-most ->AtMost)

(defrecord Any []
  Matcher
  (matches? [this actual] true)
  (description [this] "any"))

(def any ->Any)

(defrecord A [expected]
  Matcher
  (matches? [this actual] (instance? expected actual))
  (description [this] (utils/formatted "a %s" (pr-str expected))))

(def a ->A)

(defrecord Predicate [predicate]
  Matcher
  (matches? [_this actual]
    (try
      (predicate actual)
      (catch #?(:clj Exception :cljs js/Error) _e false)))
  (description [_this]
    (utils/formatted "satisfies %s predicate function"
                     (str predicate))))

(def pred ->Predicate)

(def any-args?
  "A special directive that always matches regardless of the number or form of
  arguments provided. Useful as a terminal fall-through case when mocking.

  For example:
  (providing [(inc any-args?) :placeholder-inc-result]
    (inc 1) ;=> :placeholder-inc-result
    (inc 1 2 3 4) ;=> :placeholder-inc-result)"
  ::any-args?)
