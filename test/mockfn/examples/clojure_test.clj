(ns mockfn.examples.clojure-test
  (:require [clojure.test :refer :all]
            [mockfn.clj-test :as mfn]
            [mockfn.matchers :as matchers]))

(def one-fn)
(def other-fn)

(mfn/deftest deftest-with-builtin-mocking
  (is (= :one-fn (one-fn)))
  (mfn/providing
    (one-fn) :one-fn)

  (mfn/testing "testing with built-in-mocking"
    (is (= :one-fn (one-fn)))
    (is (= :other-fn (other-fn)))
    (mfn/verifying
      (other-fn) :other-fn (matchers/exactly 1))))
