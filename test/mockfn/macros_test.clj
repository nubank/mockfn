(ns mockfn.macros-test
  (:require [clojure.test :refer :all]
            [mockfn.macros :as macros]
            [mockfn.matchers :as matchers])
  (:import (clojure.lang ExceptionInfo)))

(def one-fn)
(def another-fn)

(deftest providing-test
  (testing "mocks functions without arguments"
    (macros/providing
      [(one-fn) :mocked]
      (is (= :mocked (one-fn)))))

  (testing "mocks functions with arguments"
    (macros/providing
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
    (macros/providing
      [(one-fn) :one-fn
       (another-fn) :other-fn]
      (is (= :one-fn (one-fn)))
      (is (= :other-fn (another-fn))))))

(deftest verifying-test
  (testing "mocks functions without arguments"
    (macros/verifying
      [(one-fn) :mocked (matchers/exactly 1)]
      (is (= :mocked (one-fn)))))

  (testing "mocks functions with arguments"
    (macros/verifying
      [(one-fn :expected) :mocked (matchers/exactly 1)
       (one-fn :expected :also-expected) :also-mocked (matchers/exactly 1)]
      (is (= :mocked (one-fn :expected)))
      (is (= :also-mocked (one-fn :expected :also-expected)))
      (is (thrown-with-msg?
            ExceptionInfo #"Unexpected call"
            (one-fn :unexpected)))
      (is (thrown-with-msg?
            ExceptionInfo #"Unexpected call"
            (one-fn)))))

  (testing "mocks multiple functions at once"
    (macros/verifying
      [(one-fn) :one-fn (matchers/exactly 1)
       (another-fn) :other-fn (matchers/exactly 1)]
      (is (= :one-fn (one-fn)))
      (is (= :other-fn (another-fn)))))

  (testing "fails if calls are not performed the expected number of times"
    (is (thrown-with-msg?
          ExceptionInfo #"Function .* unexpectedly called"
          (macros/verifying
            [(one-fn) :one-fn (matchers/exactly 2)]
            (is (= :one-fn (one-fn))))))))
