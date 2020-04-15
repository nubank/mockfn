(ns mockfn.internal.macros
  (:require [mockfn.mock :as mock]))

(defn- func->func-sym
  "Extracts the symbol for the function being mocked.

  When a symbol fn is passed as argument, returns fn.
  When a (var fn) is passed as argument (such as when mocking private
  functions), returns fn instead of (var fn)."
  [func]
  (if (seq? func) (last func) func))

(defn specification->redef-bindings
  "Takes a specification of mocks to be produced for a set of functions and
  produces with-redefs bindings for each function with mocks in place of the
  original implementations."
  [specification]
  (->> specification
       (map (fn [[func mock-description]] [(func->func-sym func) `(mock/mock ~func ~mock-description)]))
       (apply concat)))

(defn bindings->specification
  "Takes a list of providing/verifying bindings and produces a specification
  describing a mock to be produced for every function in the bindings."
  [bindings]
  (reduce
   (fn [acc [[func & args] ret-val & [verify-times-expected]]]
     (-> acc
         (assoc-in [func :stubbed/function] func)
         (assoc-in [func :stubbed/calls (into [] args)]
                   (cond-> {:providing/return-value ret-val}
                     verify-times-expected
                     (assoc :verifying/times-called   `(atom 0)
                            :verifying/times-expected verify-times-expected)))))
   {} bindings))
