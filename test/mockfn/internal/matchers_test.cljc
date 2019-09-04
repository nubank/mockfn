(ns mockfn.internal.matchers-test
  (:require [clojure.test :refer [deftest testing is]]
            [mockfn.internal.matchers :as internal.matchers]
            [mockfn.matchers :as matchers]))

(def matcher (matchers/exactly :expected))

(deftest matches-args?-test
  (testing "matches empty argument lists"
    (is (true? (internal.matchers/matches-args? [] []))))

  (testing "matches equal one-argument lists"
    (is (true? (internal.matchers/matches-args? [:equal] [:equal]))))

  (testing "matches equal multi-argument lists"
    (is (true? (internal.matchers/matches-args? [:equal :equal-too] [:equal :equal-too]))))

  (testing "does not match if arity is different"
    (is (false? (internal.matchers/matches-args? [] [:unexpected])))
    (is (false? (internal.matchers/matches-args? [:equal :equal-too] [:equal]))))

  (testing "does not match if arguments are different"
    (is (false? (internal.matchers/matches-args? [:different] [:unequal])))
    (is (false? (internal.matchers/matches-args? [:equal :equal-too] [:equal :different]))))

  (testing "matches with matchers instead of literal values"
    (is (true? (internal.matchers/matches-args? [matcher] [:expected])))
    (is (true? (internal.matchers/matches-args? [matcher matcher] [:expected :expected]))))

  (testing "does not match if arguments do not satisfy matchers"
    (is (false? (internal.matchers/matches-args? [matcher] [:unexpected])))
    (is (false? (internal.matchers/matches-args? [matcher matcher] [:expected :unexpected])))))
