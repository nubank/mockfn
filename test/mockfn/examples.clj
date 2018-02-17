(ns mockfn.examples
  (:require [clojure.test :refer :all]
            [mockfn.core :refer :all]))

(def support-fn)
(defn tested-fn [& args]
  (apply support-fn args))

(deftest providing-test
  (providing [(support-fn :argument) :result]
    (is (= :result (tested-fn :argument)))))

(deftest verifying-test
  (verifying [(support-fn :argument) :result (exactly 1)]
    (is (= :result (tested-fn :argument)))))

(deftest argument-matcher-test
  (providing [(support-fn (at-least 10) (at-most 20)) 15]
    (is (= 15 (tested-fn 10 20)))))

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
