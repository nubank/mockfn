(ns mockfn.mock
  (:require [mockfn.matchers :as matchers]
            [mockfn.parser]
            [mockfn.utils :as utils]))

(defrecord Calling [function])

(defrecord CallingOriginal [])

(defn- matches-arg?
  [[expected arg]]
  (if (satisfies? matchers/Matcher expected)
    (matchers/matches? expected arg)
    (= expected arg)))

(defn- matches-args?
  [expected args]
  (let [arity-matches?    (= (count expected) (count args))
        each-arg-matches? (every? matches-arg? (map vector expected args))]
    (and arity-matches? each-arg-matches? expected)))

(defn- for-args
  [m args]
  (let [expected (some #(matches-args? % args) (keys m))]
    (if expected
      (get m expected)
      ::unexpected-call)))

(defn- remap-unbound-var [func]
  #?(:cljs (if (nil? func) ;; cljs doesn't have unbound vars
             "<unbound var>"
             func)
     :clj func))

(defn- unexpected-call [func args]
  (utils/formatted "Unexpected call to %s with args %s"
                   (remap-unbound-var func)
                   args))

(defn- get-value-for
  [func spec args]
  (when (-> spec :return-values (for-args args) #{::unexpected-call})
    (throw (ex-info (unexpected-call func args) {})))
  (-> spec :times-called (for-args args) (swap! inc))
  (-> spec :return-values (for-args args)))

(defn- return-value-for-call [func spec args]
  (let [mocked-value (get-value-for func spec args)]
    (cond
      (instance? Calling mocked-value)
      (-> mocked-value :function (apply args))

      (instance? CallingOriginal mocked-value)
      (-> spec :function (apply args))

      :default
      mocked-value)))

(defn mock [func spec]
  (with-meta
    (fn [& args] (return-value-for-call func spec (into [] args)))
    spec))

(defn- doesnt-match [func args matcher times-called]
  (utils/formatted "Expected %s with arguments %s %s, received %s."
                   (remap-unbound-var func)
                   args
                   (matchers/description matcher)
                   times-called))

(defn verify [mock]
  (doseq [args    (-> mock meta :times-expected keys)
          matcher (-> mock meta :times-expected (get args))]
    (let [times-called (-> mock meta :times-called (get args) deref)]
      (when-not (matchers/matches? matcher times-called)
        (throw (ex-info (doesnt-match (-> mock meta :function) args matcher times-called) {}))))))
