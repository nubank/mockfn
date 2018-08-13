(ns mockfn.sugar
  (:require [clojure.test :as test]
            [mockfn.macros :as macros]))

(declare providing)
(declare verifying)

(defn- providing-only?
  [form]
  (-> form first resolve #{#'providing}))

(defn- verifying-only?
  [form]
  (-> form first resolve #{#'verifying}))

(defmacro deftest
  [name & body]
  (let [providing-bindings (->> body (filter providing-only?) first rest)
        verifying-bindings (->> body (filter verifying-only?) first rest)
        actual-body        (->> body (remove providing-only?) (remove verifying-only?))]
    `(test/deftest
       ~name
       (macros/providing [~@providing-bindings]
         (macros/verifying [~@verifying-bindings]
           ~@actual-body)))))
