(ns mockfn.mock-test
  (:require [clojure.test :refer [deftest testing is]]
            [mockfn.mock :as mock]
            [mockfn.matchers :as matchers]
            [mockfn.fixtures :as fixtures])
  #?(:clj (:import (clojure.lang ExceptionInfo Keyword))))

(deftest stub-test
  (let [definition {:stubbed/calls {[]            {:providing/return-value :no-args}
                                    [:arg1]       {:providing/return-value :one-arg}
                                    [:arg1 :arg2] {:providing/return-value :two-args}
                                    [:nil]        {:providing/return-value nil}}}
        stub       (mock/mock fixtures/one-fn definition)]
    (testing "returns to expected calls with configured return values"
      (is (= :no-args (stub)))
      (is (= :one-arg (stub :arg1)))
      (is (= :two-args (stub :arg1 :arg2)))
      (is (= nil (stub :nil))))

    (testing "throws exception when called with unexpected arguments"
      (let [message-regex #?(:clj #"mockfn.fixtures/one-fn was specified as mocked"
                             :cljs #"<unbound var> was specified as mocked")]
        (is (thrown-with-msg?
              ExceptionInfo message-regex
              (stub :unexpected-a :unexpected-b)))))

    (testing "throws exception when called with 1 unexpected argument"
      (let [message-regex #?(:clj #"mockfn.fixtures/one-fn was specified as mocked"
                             :cljs #"<unbound var> was specified as mocked")]
        (is (thrown-with-msg?
              ExceptionInfo message-regex
              (stub :unexpected)))))))

(deftest mock-call-count-test
  (let [definition {:stubbed/function 'fixtures/one-fn
                    :stubbed/calls    {[]            {:providing/return-value   :no-args
                                                      :verifying/times-called   (atom 0)
                                                      :verifying/times-expected (matchers/exactly 2)}
                                       [:arg1]       {:providing/return-value   :one-arg
                                                      :verifying/times-called   (atom 0)
                                                      :verifying/times-expected (matchers/exactly 1)}
                                       [:arg1 :arg2] {:providing/return-value   :two-args
                                                      :verifying/times-called   (atom 0)
                                                      :verifying/times-expected (matchers/exactly 0)}}}
        mock       (mock/mock fixtures/one-fn definition)]
    (testing "counts the number of times that each call was performed"
      (mock) (mock) (mock :arg1)
      (is (= 2 (-> mock meta (get-in [:stubbed/calls [] :verifying/times-called]) deref)))
      (is (= 1 (-> mock meta (get-in [:stubbed/calls [:arg1] :verifying/times-called]) deref)))
      (is (= 0 (-> mock meta (get-in [:stubbed/calls [:arg1 :arg2] :verifying/times-called]) deref))))

    (testing "verifies that calls were performed the expected number of times"
      (is nil? (mock/verify mock))
      (mock :arg1 :arg2)
      (is (thrown-with-msg?
            ExceptionInfo
            #"Expected fixtures/one-fn with arguments \[:arg1 :arg2\] exactly 0 times, received 1."
            (mock/verify mock))))))

(deftest mock-match-argument-test
  (let [definition {:stubbed/function 'fixtures/one-fn
                    :stubbed/calls    {[:argument]            {:providing/return-value :equal}
                                       [(matchers/a Keyword)] {:providing/return-value :matchers-a}
                                       [(matchers/pred odd?)] {:providing/return-value :odd}
                                       [(matchers/any)]       {:providing/return-value :matchers-any}}}
        mock       (mock/mock fixtures/one-fn definition)]
    (testing "returns to expected calls with configured return values"
      (is (= :equal (mock :argument)))
      (is (= :matchers-a (mock :any-keyword)))
      (is (= :odd (mock 1)))
      (is (= :matchers-any (mock "anything"))))))

(deftest mock-fall-through
  (let [definition {:stubbed/function (fn [& args] args)
                    :stubbed/calls    {[]            {:providing/return-value (mock/calling-original)}
                                       [:arg1]       {:providing/return-value (mock/calling-original)}
                                       [:arg1 :arg2] {:providing/return-value (mock/calling-original)}}}
        mock       (mock/mock fixtures/one-fn definition)]
    (testing "returns to expected calls with configured return values"
      (is (= nil (mock)))
      (is (= [:arg1] (mock :arg1)))
      (is (= [:arg1 :arg2] (mock :arg1 :arg2))))

    (testing "throws exception when called with unexpected arguments"
      (let [message-regex #?(:clj #"mockfn.fixtures/one-fn was specified as mocked"
                             :cljs #"<unbound var> was specified as mocked")]
        (is (thrown-with-msg?
              ExceptionInfo
              message-regex
              (mock :unexpected)))))))

(deftest private-fn-mock-test
  (let [definition {:stubbed/calls {[]      {:providing/return-value :no-args}
                                    [:arg1] {:providing/return-value :one-arg}}}
        mock       (mock/mock #'fixtures/private-fn definition)]
    (testing "returns to expected calls with configured return values"
      (is (= :no-args (mock)))
      (is (= :one-arg (mock :arg1))))))
