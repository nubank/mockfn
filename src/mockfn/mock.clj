(ns mockfn.mock
  (:require [mockfn.matchers :as matchers]))

(defn- unexpected-call [func args]
  (format "Unexpected call to %s with args %s." func args))

(defn- doesnt-match [function args matcher times-called]
  (format "Expected %s with arguments %s %s, received %s."
          function args (matchers/description matcher) times-called))

(defn- return-value-for
  [func spec args]
  (when-not (-> spec :return-values (contains? args))
    (throw (ex-info (unexpected-call func args) {})))
  (-> spec (get-in [:times-called args]) (swap! inc))
  (get-in spec [:return-values args]))

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
