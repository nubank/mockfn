(ns mockfn.core-test
  (:require [clojure.test :refer :all]
            [mockfn.core :refer :all])
  (:import (clojure.lang ExceptionInfo)))

(def one-fn)
(def another-fn)

(deftest providing-test
  (testing "stubs one function without arguments"
    (providing
      [(one-fn) :stubbed]
      (is (= :stubbed (one-fn)))))

  (testing "stubs multiple functions at once"
    (providing
      [(one-fn) :one-fn
       (another-fn) :other-fn]
      (is (= :one-fn (one-fn)))
      (is (= :other-fn (another-fn))))))
