(ns mockfn.clj-test-test
  (:require [clojure.test :refer :all]
            [mockfn.clj-test :as mfn]
            [mockfn.matchers :as matchers]))

(def tests-run (atom #{}))

(declare one-fn)
(declare other-fn)

(mfn/deftest deftest-test
  (swap! tests-run conj :deftest))

(mfn/deftest deftest-providing-test
  (swap! tests-run conj (one-fn))
  (mfn/providing
    (one-fn) :deftest-providing))

(mfn/deftest deftest-verifying-test
  (swap! tests-run conj (one-fn))
  (mfn/verifying
    (one-fn) :deftest-verifying (matchers/exactly 1)))

(mfn/deftest deftest-providing-and-verifying-test
  (swap! tests-run conj (one-fn))
  (swap! tests-run conj (other-fn))
  (mfn/providing
    (one-fn) :deftest-providing-with-verifying)
  (mfn/verifying
    (other-fn) :deftest-verifying-with-providing (matchers/exactly 1)))

(mfn/deftest testing-test
  (mfn/testing "testing"
    (swap! tests-run conj :testing)))

(mfn/deftest testing-providing-test
  (mfn/testing "testing-providing"
    (swap! tests-run conj (one-fn))
    (mfn/providing
      (one-fn) :testing-providing)))

(mfn/deftest testing-verifying-test
  (mfn/testing "testing-verifying"
    (swap! tests-run conj (one-fn))
    (mfn/verifying
      (one-fn) :testing-verifying (matchers/exactly 1))))

(mfn/deftest testing-providing-and-verifying-test
  (mfn/testing "testing-providing-and-verifying"
    (swap! tests-run conj (one-fn))
    (swap! tests-run conj (other-fn))
    (mfn/providing
      (one-fn) :testing-providing-with-verifying)
    (mfn/verifying
      (other-fn) :testing-verifying-with-providing (matchers/exactly 1))))

(mfn/deftest deftest-testing-test
  (mfn/testing "deftest-testing"
    (swap! tests-run conj (one-fn))
    (swap! tests-run conj (other-fn))
    (mfn/providing
      (one-fn) :deftest-testing-pt1))
  (mfn/providing
    (other-fn) :deftest-testing-pt2))

(def expected-tests-run
  #{:deftest
    :deftest-providing
    :deftest-verifying
    :deftest-providing-with-verifying
    :deftest-verifying-with-providing
    :testing
    :testing-providing
    :testing-verifying
    :testing-providing-with-verifying
    :testing-verifying-with-providing
    :deftest-testing-pt1
    :deftest-testing-pt2})

(defn teardown []
  (is (= @tests-run expected-tests-run))
  (reset! tests-run #{}))

(defn once-fixture [f]
  (f) (teardown))

(use-fixtures :once once-fixture)
