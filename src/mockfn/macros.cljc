(ns mockfn.macros
  #?(:cljs (:require-macros [mockfn.macros]))
  (:require [mockfn.mock :as mock]
            [mockfn.internal.macros :as internal.macro]))

(defn calling
  "Invoke mocked value as a function instead of returning it."
  [func] (mock/calling func))

(def unmocked
  "Invoke the original implementation of the mocked function."
  (mock/calling-original))

(defmacro providing
  "Mocks functions."
  [bindings & body]
  `(with-redefs ~(->> bindings (partition 2) internal.macro/bindings->specification internal.macro/specification->redef-bindings)
     ~@body))

(defmacro verifying
  "Mocks functions and verifies calls."
  [bindings & body]
  (let [specs# (->> bindings (partition 3) internal.macro/bindings->specification)]
    `(with-redefs ~(internal.macro/specification->redef-bindings specs#)
       (let [res# (do ~@body)]
         (doseq [mock# (keys ~specs#)]
           (mock/verify mock#))
         res#))))
