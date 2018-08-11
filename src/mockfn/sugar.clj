(ns mockfn.sugar
  (:require [clojure.test :as test]))

(defmacro deftest
  [name & body]
  `(test/deftest ~name ~@body))
