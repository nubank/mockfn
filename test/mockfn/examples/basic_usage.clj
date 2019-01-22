(ns mockfn.examples.basic-usage
  (:require [clojure.test :refer :all]
            [mockfn.macros :as mfn]
            [mockfn.matchers :as matchers])
  (:import (clojure.lang ExceptionInfo)))

(def one-fn)
(def other-fn)

(deftest examples-test
  (testing "providing"
    (mfn/providing [(one-fn) :result]
      (is (= :result (one-fn)))))

  (testing "providing - one function, different arguments"
    (mfn/providing [(one-fn :argument-1) :result-1
                    (one-fn :argument-2) :result-2]
      (is (= :result-1 (one-fn :argument-1)))
      (is (= :result-2 (one-fn :argument-2)))))

  (testing "providing with more than one function"
    (mfn/providing [(one-fn :argument) :result-1
                    (other-fn :argument) :result-2]
      (is (= :result-1 (one-fn :argument)))
      (is (= :result-2 (other-fn :argument)))))

  (testing "providing with explicit predicates (via `pred`) for argument
           matching"
    (mfn/providing [(one-fn (matchers/pred even?)) :even]
      (is (= :even (one-fn 2)))))

  (testing "providing with implicit predicates (via extending `Fn`) for
           argument matching. Matching exact functions works via `exactly`"
    (mfn/providing [(one-fn odd?) :odd
                    (one-fn (matchers/exactly inc)) :inc-fn]
      (is (= :odd (one-fn 1)))
      (is (= :inc-fn (one-fn inc)))))

  (testing "verifying"
    (mfn/verifying [(one-fn :argument) :result (matchers/exactly 1)]
      (is (= :result (one-fn :argument)))))

  (testing "argument matchers"
    (mfn/providing [(one-fn (matchers/at-least 10) (matchers/at-most 20)) 15]
      (is (= 15 (one-fn 12 18)))))

  (testing "nested mocks"
    (mfn/providing [(one-fn :argument-1) :result-1]
      (mfn/providing [(one-fn :argument-2) :result-2
                      (other-fn :argument-3) :result-3]
        (is (thrown? ExceptionInfo (one-fn :argument-1)))
        (is (= :result-2 (one-fn :argument-2)))
        (is (= :result-3 (other-fn :argument-3))))
      (is (= :result-1 (one-fn :argument-1))))))
