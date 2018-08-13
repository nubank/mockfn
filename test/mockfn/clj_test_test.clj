(ns mockfn.clj-test-test
  (:require [clojure.test :refer :all]
            [mockfn.clj-test :as mfn]
            [mockfn.matchers :as matchers]))

(def tests-run (atom #{}))

(declare one-fn)
(declare other-fn)

(mfn/deftest deftest-test
  (swap! tests-run conj :deftest))

(mfn/deftest providing-test
  (swap! tests-run conj (one-fn))
  (mfn/providing
    (one-fn) :deftest-providing))

(mfn/deftest verifying-test
  (swap! tests-run conj (one-fn))
  (mfn/verifying
    (one-fn) :deftest-verifying (matchers/exactly 1)))

(mfn/deftest providing-and-verifying-test
  (swap! tests-run conj (one-fn))
  (swap! tests-run conj (other-fn))
  (mfn/providing
    (one-fn) :deftest-providing-with-verifying)
  (mfn/verifying
    (other-fn) :deftest-verifying-with-providing (matchers/exactly 1)))

(def expected-tests-run
  #{:deftest
    :deftest-providing
    :deftest-verifying
    :deftest-providing-with-verifying
    :deftest-verifying-with-providing})

(defn teardown []
  (is (= @tests-run expected-tests-run))
  (reset! tests-run #{}))

(defn once-fixture [f]
  (f) (teardown))

(use-fixtures :once once-fixture)
