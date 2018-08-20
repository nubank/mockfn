(ns mockfn.clj-test
  (:require [clojure.test :as test]
            [mockfn.macros :as macros]))

(declare providing)
(declare verifying)

(defn- only?
  [symbol form]
  (-> form first resolve #{symbol}))

(def ^:private providing-only? (partial only? #'providing))
(def ^:private verifying-only? (partial only? #'verifying))

(defmacro with-mocking
  [base-macro name & body]
  (let [providing-bindings (->> body (filter providing-only?) first rest)
        verifying-bindings (->> body (filter verifying-only?) first rest)
        actual-body        (->> body (remove providing-only?) (remove verifying-only?))]
    `(~base-macro
       ~name
       (macros/providing [~@providing-bindings]
         (macros/verifying [~@verifying-bindings]
           ~@actual-body)))))

(defmacro deftest
  [name & body]
  `(with-mocking test/deftest ~name ~@body))

(defmacro testing
  [string & body]
  `(with-mocking test/testing ~string ~@body))
