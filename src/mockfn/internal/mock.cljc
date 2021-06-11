(ns mockfn.internal.mock
  (:require [clojure.string :as string]
            [mockfn.internal.utils :as utils]
            [mockfn.internal.matchers :as internal.matchers]
            [mockfn.matchers :as matchers]))

(defrecord Calling [function])

(defrecord CallingOriginal [])

(defn- matching-fn-for [args]
  (fn [matchers]
    (when (internal.matchers/matches-args? matchers args)
      matchers)))

(defn- for-args
  "Takes a map `arg-matchers->result` where the keys are lists of matchers.
  Retrieves from this map a value for which the list args fulfill the list of
  matchers in the key.

  If args doesn't satisfy any list of matchers, returns ::unexpected-call."
  [arg-matchers->result args]
  (if-let [expected (some (matching-fn-for args) (keys arg-matchers->result))]
    (get arg-matchers->result expected)
    ::unexpected-call))

(defn- func-or-unbound-var
  "Returns the given function or an \"<unbound var>\" string if the
  function is nil (cljs doesn't have unbound vars)."
  [func]
  (or (some-> func str (string/replace-first "Unbound: #'" ""))
      "<unbound var>"))

(defn- unexpected-call-msg
  "Exception message for unexpected call."
  [func args]
  (let [f (func-or-unbound-var func)]
    (utils/formatted
      "%s was specified as mocked but none of the cases matched the %d provided argument(s):
  %s

Either
 - specify a mock case for those arguments
 - specify falling through to the original functionality via a terminal match clause:
  `(%s mockfn.matchers/any-args?) mockfn.macros/fall-through"
      f
      (count args)
      args
      f)))

(defn- map-vals [f m]
  (into {} (for [[k v] m] [k (f v)])))

(defn- extract [spec args prop]
  (let [arg-matchers->result (map-vals #(get % prop) (:stubbed/calls spec))]
    (for-args arg-matchers->result args)))

(defn- ensure-expected-call
  "Throws an exception if the given call is unexpected."
  [func spec args]
  (when (#{::unexpected-call} (extract spec args :providing/return-value))
    (throw (ex-info (unexpected-call-msg func args) {}))))

(defn- increase-call-count
  "Tracks the number of times a specific call is performed."
  [spec args]
  (some-> (extract spec args :verifying/times-called)
          (swap! inc)))

(defn call->ret-val
  "Produces the return value for a mocked call.

  Tracks the number of times a specific call is made for a given function
  and set of parameters fulfilling matcher criteria.
  Throws an exception when an unexpected call is received."
  [func spec args]
  (ensure-expected-call func spec args)
  (increase-call-count spec args)

  (let [ret-val (extract spec args :providing/return-value)]
    (cond
      (instance? Calling ret-val)
      (-> ret-val :function (apply args))

      (instance? CallingOriginal ret-val)
      (-> spec :stubbed/function (apply args))

      :default
      ret-val)))

(defn matcher-failure-ex-msg
  "Exception message for call count not matching the expectation."
  [func args matcher times-called]
  (utils/formatted "Expected %s with arguments %s %s, received %s."
                   (func-or-unbound-var func)
                   args
                   (matchers/description matcher)
                   times-called))

(defn mock->spec
  "Retrieves the specification for the given mock."
  [mock]
  (cond-> mock
          (var? mock) deref
          :then meta))
