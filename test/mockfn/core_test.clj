(ns mockfn.core-test
  (:require [clojure.test :refer :all]
            [mockfn.core :refer :all])
  (:import (clojure.lang ExceptionInfo)))

(def one-fn)
(def another-fn)

(deftest providing-test
  (testing "mocks functions without arguments"
    (providing
      [(one-fn) :mocked]
      (is (= :mocked (one-fn)))))

  (testing "mocks functions with arguments"
    (providing
      [(one-fn :expected) :mocked
       (one-fn :expected :also-expected) :also-mocked]
      (is (= :mocked (one-fn :expected)))
      (is (= :also-mocked (one-fn :expected :also-expected)))
      (is (thrown-with-msg?
            ExceptionInfo #"Unexpected call"
            (one-fn :unexpected)))
      (is (thrown-with-msg?
            ExceptionInfo #"Unexpected call"
            (one-fn)))))

  (testing "mocks multiple functions at once"
    (providing
      [(one-fn) :one-fn
       (another-fn) :other-fn]
      (is (= :one-fn (one-fn)))
      (is (= :other-fn (another-fn))))))
