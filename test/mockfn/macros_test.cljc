(ns mockfn.macros-test
  (:require [clojure.test :refer [deftest testing is]]
            [mockfn.macros :as macros]
            [mockfn.matchers :as matchers]
            [mockfn.fixtures :as fixtures])
  #?(:clj (:import (clojure.lang ExceptionInfo Keyword))))

(deftest providing-test
  (testing "mocks functions without arguments"
    (macros/providing
     [(fixtures/one-fn) :mocked]
     (is (= :mocked (fixtures/one-fn)))))

  (testing "returns return value of body"
    (is (true? (macros/providing
                [(fixtures/one-fn) :mocked]
                (= :mocked (fixtures/one-fn)))))
    (is (false? (macros/providing
                [(fixtures/one-fn) :mocked]
                (= :other (fixtures/one-fn))))))

  (testing "mocks functions with arguments"
    (macros/providing
      [(fixtures/one-fn :expected) :mocked
       (fixtures/one-fn :expected :also-expected) :also-mocked]
      (is (= :mocked (fixtures/one-fn :expected)))
      (is (= :also-mocked (fixtures/one-fn :expected :also-expected)))
      (is (thrown-with-msg?
            ExceptionInfo #"Unbound: #'mockfn.fixtures/one-fn was specified as mocked"
            (fixtures/one-fn :unexpected)))
      (is (thrown-with-msg?
            ExceptionInfo #"Unbound: #'mockfn.fixtures/one-fn was specified as mocked"
            (fixtures/one-fn)))))

  (testing "mocks functions with argument matchers"
    (macros/providing
      [(fixtures/one-fn (matchers/a Keyword)) :mocked]
      (is (= :mocked (fixtures/one-fn :expected)))
      (is (thrown-with-msg?
            ExceptionInfo #"Unbound: #'mockfn.fixtures/one-fn was specified as mocked"
            (fixtures/one-fn "unexpected")))))

  (testing "mocks multiple functions at once"
    (macros/providing
      [(fixtures/one-fn) :fixtures/one-fn
       (fixtures/other-fn) :fixtures/other-fn]
      (is (= :fixtures/one-fn (fixtures/one-fn)))
      (is (= :fixtures/other-fn (fixtures/other-fn)))))

  (testing "mocks private functions"
    (macros/providing
      [(#'fixtures/private-fn) :mocked]
      (is (= :mocked (#'fixtures/private-fn))))))

(deftest verifying-test
  (testing "mocks functions without arguments"
    (macros/verifying
      [(fixtures/one-fn) :mocked (matchers/exactly 1)]
      (is (= :mocked (fixtures/one-fn)))))

  (testing "returns return value of body"
    (is (true? (macros/verifying
                [(fixtures/one-fn) :mocked (matchers/exactly 1)]
                (= :mocked (fixtures/one-fn)))))
    (is (false? (macros/verifying
                [(fixtures/one-fn) :mocked (matchers/exactly 1)]
                (= :other (fixtures/one-fn))))))

  (testing "mocks functions with arguments"
    (macros/verifying
      [(fixtures/one-fn :expected) :mocked (matchers/exactly 1)
       (fixtures/one-fn :expected :also-expected) :also-mocked (matchers/exactly 1)]
      (is (= :mocked (fixtures/one-fn :expected)))
      (is (= :also-mocked (fixtures/one-fn :expected :also-expected)))
      (is (thrown-with-msg?
            ExceptionInfo #"Unbound: #'mockfn.fixtures/one-fn was specified as mocked"
            (fixtures/one-fn :unexpected)))
      (is (thrown-with-msg?
            ExceptionInfo #"Unbound: #'mockfn.fixtures/one-fn was specified as mocked"
            (fixtures/one-fn)))))

  (testing "mocks functions with argument matchers"
    (macros/verifying
      [(fixtures/one-fn (matchers/a Keyword)) :mocked (matchers/exactly 1)]
      (is (= :mocked (fixtures/one-fn :expected)))
      (is (thrown-with-msg?
            ExceptionInfo #"Unbound: #'mockfn.fixtures/one-fn was specified as mocked"
            (fixtures/one-fn "unexpected")))))

  (testing "mocks multiple functions at once"
    (macros/verifying
      [(fixtures/one-fn) :fixtures/one-fn (matchers/exactly 1)
       (fixtures/other-fn) :fixtures/other-fn (matchers/exactly 1)]
      (is (= :fixtures/one-fn (fixtures/one-fn)))
      (is (= :fixtures/other-fn (fixtures/other-fn)))))

  (testing "fails if calls are not performed the expected number of times"
    (is (thrown-with-msg?
          ExceptionInfo #"Expected .* with arguments"
          (macros/verifying
            [(fixtures/one-fn) :fixtures/one-fn (matchers/exactly 2)]
            (is (= :fixtures/one-fn (fixtures/one-fn)))))))

  (testing "mocks private functions"
    (macros/verifying
      [(#'fixtures/private-fn) :mocked-private (matchers/exactly 1)]
      (is (= :mocked-private (#'fixtures/private-fn)))))

  (testing "matches anon fn arguments"
    (macros/verifying [(fixtures/one-fn (fn [x] (= 1 x))) :a (matchers/exactly 1)]
                      (is (= :a (fixtures/one-fn 1))))

    (macros/verifying [(fixtures/one-fn (fn [x] (= 1 x))) :a (matchers/exactly 1)
                       (fixtures/one-fn (fn [x] (= 2 x))) :a (matchers/exactly 1)]
                      (is (= :a (fixtures/one-fn 1)))
                      (is (= :a (fixtures/one-fn 2)))))

  (testing "fails if calls to private functions are not performed the expected number of times"
    (is (thrown-with-msg?
          ExceptionInfo #"Expected .* with arguments"
          (macros/verifying
            [(#'fixtures/private-fn) :mocked (matchers/exactly 2)]
            (is (= :mocked (#'fixtures/private-fn))))))))

(macros/verifying
  [(fixtures/one-fn (matchers/a Keyword)) :mocked (matchers/exactly 1)]
  (fixtures/one-fn "unexpected"))
