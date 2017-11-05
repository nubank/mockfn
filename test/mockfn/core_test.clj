(ns mockfn.core-test
  (:require [clojure.test :refer :all]
            [mockfn.core :refer :all])
  (:import (clojure.lang ExceptionInfo)))

(defn one-fn [])
(defn another-fn [])

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

(deftest verifying-test
  (testing "mocks functions without arguments"
    (verifying
      [(one-fn) :mocked (exactly 1)]
      (is (= :mocked (one-fn)))))

  (testing "mocks functions with arguments"
    (verifying
      [(one-fn :expected) :mocked (exactly 1)
       (one-fn :expected :also-expected) :also-mocked (exactly 1)]
      (is (= :mocked (one-fn :expected)))
      (is (= :also-mocked (one-fn :expected :also-expected)))
      (is (thrown-with-msg?
            ExceptionInfo #"Unexpected call"
            (one-fn :unexpected)))
      (is (thrown-with-msg?
            ExceptionInfo #"Unexpected call"
            (one-fn)))))

  (testing "mocks multiple functions at once"
    (verifying
      [(one-fn) :one-fn (exactly 1)
       (another-fn) :other-fn (exactly 1)]
      (is (= :one-fn (one-fn)))
      (is (= :other-fn (another-fn)))))

  (testing "fails if calls are not performed the expected number of times"
    (is (thrown-with-msg?
          ExceptionInfo #"Function .* unexpectedly called"
          (verifying
            [(one-fn) :one-fn (exactly 2)]
            (is (= :one-fn (one-fn))))))))
