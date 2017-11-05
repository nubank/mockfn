(ns mockfn.core
  (:require [mockfn.internals.mock :as mock]))

(defn- as-redefs
  [func->definition]
  (->> func->definition
       (map (fn [[func definition]] [func `(mock/mock ~func ~definition)]))
       (apply concat)))

(defn- func->spec
  [bindings]
  (reduce
    (fn [acc [[func & args] ret-val]]
      (-> acc
          (assoc-in [func :return-values (into [] args)] ret-val)
          (assoc-in [func :times-called (into [] args)] `(atom 0))))
    {} bindings))

(defmacro providing
  "Mocks functions."
  [bindings & body]
  `(with-redefs ~(->> bindings (partition 2) func->spec as-redefs)
     ~@body))
