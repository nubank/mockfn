(ns mockfn.core-test
  (:require [clojure.test :refer :all]
            [mockfn.core :refer :all])
  (:import (clojure.lang ExceptionInfo)))

(def one-fn)
(def another-fn)

(deftest providing-test
  (testing "stubs functions without arguments"
    (providing
      [(one-fn) :stubbed]
      (is (= :stubbed (one-fn)))))

  (testing "stubs functions with arguments"
    (providing
      [(one-fn :expected) :stubbed
       (one-fn :expected :also-expected) :also-stubbed]
      (is (= :stubbed (one-fn :expected)))
      (is (= :also-stubbed (one-fn :expected :also-expected)))
      (is (thrown-with-msg?
            ExceptionInfo #"Unexpected call"
            (one-fn :unexpected)))
      (is (thrown-with-msg?
            ExceptionInfo #"Unexpected call"
            (one-fn)))))

  (testing "stubs multiple functions at once"
    (providing
      [(one-fn) :one-fn
       (another-fn) :other-fn]
      (is (= :one-fn (one-fn)))
      (is (= :other-fn (another-fn))))))
