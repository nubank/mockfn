(ns mockfn.examples
  (:require [clojure.test :refer :all]
            [mockfn.core :refer :all])
  (:import (clojure.lang ExceptionInfo)))

(def one-fn)
(def other-fn)

(deftest examples-test
  (testing "providing"
    (providing [(one-fn) :result]
      (is (= :result (one-fn)))))

  (testing "providing - one function, different arguments"
    (providing [(one-fn :argument-1) :result-1
                (one-fn :argument-2) :result-2]
      (is (= :result-1 (one-fn :argument-1)))
      (is (= :result-2 (one-fn :argument-2)))))

  (testing "providing with more than one function"
    (providing [(one-fn :argument) :result-1
                (other-fn :argument) :result-2]
      (is (= :result-1 (one-fn :argument)))
      (is (= :result-2 (other-fn :argument)))))

  (testing "verifying"
    (verifying [(one-fn :argument) :result (exactly 1)]
      (is (= :result (one-fn :argument)))))

  (testing "argument matchers"
    (providing [(one-fn (at-least 10) (at-most 20)) 15]
      (is (= 15 (one-fn 12 18)))))

  (testing "nested mocks"
    (providing [(one-fn :argument-1) :result-1]
      (providing [(one-fn :argument-2) :result-2
                  (other-fn :argument-3) :result-3]
        (is (thrown? ExceptionInfo (one-fn :argument-1)))
        (is (= :result-2 (one-fn :argument-2)))
        (is (= :result-3 (other-fn :argument-3))))
      (is (= :result-1 (one-fn :argument-1))))))

(def power-available?)
(def turn-on-lightbulb)
(def turn-off-lightbulb)

(defn light-switch
  [state lightbulb electricity]
  (if (and (power-available? electricity) (= :on state))
    (turn-on-lightbulb lightbulb electricity)
    (turn-off-lightbulb lightbulb)))

(deftest providing-and-verifying-test
  (testing "when there is power"
    (providing [(power-available? 'electricity) true]

      (testing "turn the lightbulb on when switch is toggled to :on"
        (verifying [(turn-on-lightbulb 'lightbulb 'electricity) :light (exactly 1)]
          (is (= :light (light-switch :on 'lightbulb 'electricity)))))

      (testing "turn the lightbulb off when switch is toggled to :off"
        (verifying [(turn-off-lightbulb 'lightbulb) :dark (exactly 1)]
          (is (= :dark (light-switch :off 'lightbulb 'electricity)))))))

  (testing "when there is no power"
    (providing [(power-available? 'electricity) false]

      (testing "turn the lightbulb off when switch is toggled to :on"
        (verifying [(turn-off-lightbulb 'lightbulb) :dark (exactly 1)]
          (is (= :dark (light-switch :on 'lightbulb 'electricity)))))

      (testing "turn the lightbulb off when switch is toggled to :off"
        (verifying [(turn-off-lightbulb 'lightbulb) :dark (exactly 1)]
          (is (= :dark (light-switch :off 'lightbulb 'electricity))))))))
