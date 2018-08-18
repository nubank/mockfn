(ns mockfn.integration
  (:require [clojure.test :refer :all]
            [mockfn.clj-test :as mfn]
            [mockfn.matchers :as matchers]))

(def power-available?)
(def turn-on-lightbulb)
(def turn-off-lightbulb)

(defn light-switch
  [state lightbulb electricity]
  (if (and (power-available? electricity) (= :on state))
    (turn-on-lightbulb lightbulb electricity)
    (turn-off-lightbulb lightbulb)))

(mfn/deftest integration-test
  (mfn/testing "when there is power"
    (mfn/testing "turn the lightbulb on when switch is toggled to :on"
      (is (= :light (light-switch :on 'lightbulb 'electricity)))
      (mfn/verifying
        (turn-on-lightbulb 'lightbulb 'electricity) :light (matchers/exactly 1)))

    (mfn/testing "turn the lightbulb off when switch is toggled to :off"
      (is (= :dark (light-switch :off 'lightbulb 'electricity)))
      (mfn/verifying
        (turn-off-lightbulb 'lightbulb) :dark (matchers/exactly 1)))

    (mfn/providing
      (power-available? 'electricity) true))

  (mfn/testing "when there is no power"
    (mfn/testing "turn the lightbulb off when switch is toggled to :on"
      (is (= :dark (light-switch :on 'lightbulb 'electricity)))
      (mfn/verifying
        (turn-off-lightbulb 'lightbulb) :dark (matchers/exactly 1)))

    (mfn/testing "turn the lightbulb off when switch is toggled to :off"
      (is (= :dark (light-switch :off 'lightbulb 'electricity)))
      (mfn/verifying
        (turn-off-lightbulb 'lightbulb) :dark (matchers/exactly 1)))

    (mfn/providing
      (power-available? 'electricity) false)))
