(ns mockfn.sugar-test
  (:require [clojure.test :refer :all]
            [mockfn.sugar :as mfn]))

(def tests-run (atom #{}))

(mfn/deftest deftest-test
  (swap! tests-run conj :deftest))

(defn teardown []
  (is (= @tests-run #{:deftest}))
  (reset! tests-run #{}))

(defn once-fixture [f]
  (f) (teardown))

(use-fixtures :once once-fixture)
