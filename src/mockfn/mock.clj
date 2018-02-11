(ns mockfn.mock
  (:require [mockfn.matchers :as matchers]))

(defn- unexpected-call [func args]
  (format "Unexpected call to %s with args %s." func args))

(defn- doesnt-match [function args matcher times-called]
  (format "Expected %s with arguments %s %s, received %s."
          function args (matchers/description matcher) times-called))

(defn- matches-args?
  [args expected]
  (= expected args))

(defn- first-matching-args
  [args]
  (fn [acc expected]
    (or acc (when (matches-args? args expected) expected))))

(defn- for-args
  [m args]
  (let [expected (reduce (first-matching-args args) nil (keys m))]
    (if expected
      (get m expected)
      ::unexpected-call)))

(defn- return-value-for
  [func spec args]
  (when (-> spec :return-values (for-args args) #{::unexpected-call})
    (throw (ex-info (unexpected-call func args) {})))
  (-> spec :times-called (for-args args) (swap! inc))
  (-> spec :return-values (for-args args)))

(defn mock [func spec]
  (with-meta
    (fn [& args] (return-value-for func spec (into [] args)))
    spec))

(defn verify [mock]
  (doseq [args    (-> mock meta :times-expected keys)
          matcher (-> mock meta :times-expected (get args))]
    (let [times-called (-> mock meta :times-called (get args) deref)]
      (when-not (matchers/matches? matcher times-called)
        (throw (ex-info (doesnt-match (-> mock meta :function) args matcher times-called) {}))))))
