(ns mockfn.matchers-test
  (:require [clojure.test :refer :all]
            [mockfn.matchers :as matchers]))

(deftest exactly-test
  (let [exactly (matchers/exactly 1)]
    (testing "returns whether actual is equal to expected"
      (is (true? (matchers/matches? exactly 1)))
      (is (false? (matchers/matches? exactly 2))))
    (testing "provides an informative string representation"
      (is (= "exactly 1 times" (matchers/description exactly))))))

(deftest at-least-test
  (let [at-least (matchers/at-least 2)]
    (testing "returns whether actual is equal to expected"
      (is (true? (matchers/matches? at-least 2)))
      (is (true? (matchers/matches? at-least 3)))
      (is (false? (matchers/matches? at-least 1))))
    (testing "provides an informative string representation"
      (is (= "at least 2 times" (matchers/description at-least))))))
