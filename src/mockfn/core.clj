(ns mockfn.core
  (:require [mockfn.internals.misc :as misc]
            [mockfn.internals.stub :as stub]))

(defn- to-redefinition
  [[function args->ret-val]]
  [function `(stub/stub ~function ~args->ret-val)])

(defn- call->args->ret-val
  [[[_ & args] ret-val]]
  {(into [] args) ret-val})

(defn- redefinitions
  [bindings]
  (->> bindings
       (partition 2)
       (group-by ffirst)
       (misc/map-vals #(map call->args->ret-val %))
       (misc/map-vals #(apply merge %))
       (map to-redefinition)
       (apply concat)))

(defmacro providing
  "I don't do a whole lot."
  [bindings & body]
  `(with-redefs ~(redefinitions bindings)
     (do ~@body)))
