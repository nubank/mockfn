(ns mockfn.matchers-test
  (:require [clojure.test :refer :all]
            [mockfn.matchers :as matchers]))

(deftest exactly-test
  (let [exactly (matchers/exactly :expected)]
    (testing "returns whether actual is equal to expected"
      (is (true? (matchers/matches? exactly :expected)))
      (is (false? (matchers/matches? exactly :unexpected))))
    (testing "provides an informative string representation"
      (is (= "exactly :expected" (matchers/description exactly))))))
