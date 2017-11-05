(ns mockfn.core
  (:require [mockfn.internals.stub :as stub]))

(defn- as-redefs
  [func->definition]
  (->> func->definition
       (map (fn [[func definition]] [func `(stub/stub ~func ~definition)]))
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
  "Stub functions."
  [bindings & body]
  `(with-redefs ~(->> bindings (partition 2) func->spec as-redefs)
     ~@body))
