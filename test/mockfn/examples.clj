(ns mockfn.examples
  (:require [clojure.test :refer :all]
            [mockfn.core :refer :all]))

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
