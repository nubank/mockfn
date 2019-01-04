(ns mockfn.matchers)

(defprotocol Matcher
  (matches? [this actual])
  (description [this]))

(defrecord Exactly [expected]
  Matcher
  (matches? [this actual] (= expected actual))
  (description [this] (format "exactly %s times" expected)))

(def exactly ->Exactly)

(defrecord AtLeast [expected]
  Matcher
  (matches? [this actual] (>= actual expected))
  (description [this] (format "at least %s times" expected)))

(def at-least ->AtLeast)

(defrecord AtMost [expected]
  Matcher
  (matches? [this actual] (<= actual expected))
  (description [this] (format "at most %s times" expected)))

(def at-most ->AtMost)

(defrecord Any []
  Matcher
  (matches? [this actual] true)
  (description [this] "any"))

(def any ->Any)

(defrecord A [expected]
  Matcher
  (matches? [this actual] (instance? expected actual))
  (description [this] (format "a %s" (pr-str expected))))

(def a ->A)

(defrecord Predicate [predicate]
  Matcher
  (matches? [_this actual]
    (try
      (predicate actual)
      (catch Exception _e false)))
  (description [_this]
    (format "satisfies %s predicate function"
            (str predicate))))

(def pred ->Predicate)
