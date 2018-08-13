(ns mockfn.sugar
  (:require [clojure.test :as test]
            [mockfn.macros :as macros]))

(declare providing)

(defn- providing-only?
  [form]
  (-> form first resolve #{#'providing}))

(defmacro deftest
  [name & body]
  (let [providing-body (->> body (filter providing-only?) first rest)
        actual-body    (remove providing-only? body)]
    `(test/deftest
       ~name
       (macros/providing [~@providing-body]
         ~@actual-body))))
