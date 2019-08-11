(ns mockfn.macros
  #?(:cljs
     (:require-macros [mockfn.macros]))
  (:require [mockfn.mock :as mock]))

(defn- func->func-sym
  "Extracts the symbol for the function being mocked.

  When a symbol fn is passed as argument, returns fn.
  When a (var fn) is passed as argument (such as when mocking private
  functions), returns fn instead of (var fn)."
  [func]
  (if (seq? func) (last func) func))

(defn- as-redefs
  [func->definition]
  (->> func->definition
       (map (fn [[func definition]] [(func->func-sym func) `(mock/mock ~func ~definition)]))
       (apply concat)))

(defn- func->spec
  [bindings]
  (reduce
    (fn [acc [[func & args] ret-val & times-expected]]
      (-> acc
          (assoc-in [func :function] func)
          (assoc-in [func :return-values (into [] args)] ret-val)
          (assoc-in [func :times-called (into [] args)] `(atom 0))
          (assoc-in [func :times-expected (into [] args)] (into [] times-expected))))
    {} bindings))

(defn calling
  "Invoke mocked value as a function instead of returning it."
  [func] (mock/->Calling func))

(def unmocked
  "Invoke the original implementation of the mocked function."
  (mock/->CallingOriginal))

(defmacro providing
  "Mocks functions."
  [bindings & body]
  `(with-redefs ~(->> bindings (partition 2) func->spec as-redefs)
     ~@body))

(defmacro verifying
  "Mocks functions and verifies calls."
  [bindings & body]
  (let [specs# (->> bindings (partition 3) func->spec)
        mocks# (->> specs# keys)]
    `(with-redefs ~(as-redefs specs#)
       ~@body
       (doseq [mock# (keys ~specs#)]
         (mock/verify mock#)))))
