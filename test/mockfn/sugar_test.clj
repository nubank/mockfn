(ns mockfn.sugar-test
  (:require [clojure.test :refer :all]
            [mockfn.sugar :as mfn]))

(def tests-run (atom #{}))

(declare one-fn)

(mfn/deftest deftest-test
  (swap! tests-run conj :deftest))

(mfn/deftest providing-test
  (swap! tests-run conj (one-fn))
  (mfn/providing
    (one-fn) :deftest-providing))

(defn teardown []
  (is (= @tests-run #{:deftest :deftest-providing}))
  (reset! tests-run #{}))

(defn once-fixture [f]
  (f) (teardown))

(use-fixtures :once once-fixture)
