(ns mockfn.mock-test
  (:require [clojure.test :refer :all]
            [mockfn.mock :as mock])
  (:import (clojure.lang ExceptionInfo)))

(def one-fn)

(deftest mock-test
  (let [definition {:return-values {[]            :no-args
                                    [:arg1]       :one-arg
                                    [:arg1 :arg2] :two-args}
                    :times-called  {[]            (atom 0)
                                    [:arg1]       (atom 0)
                                    [:arg1 :arg2] (atom 0)}}
        mock       (mock/mock one-fn definition)]
    (testing "returns to expected calls with configured return values"
      (is (= :no-args (mock)))
      (is (= :one-arg (mock :arg1)))
      (is (= :two-args (mock :arg1 :arg2))))

    (testing "throws exception when called with unexpected arguments"
      (is (thrown-with-msg?
            ExceptionInfo #"Unexpected call to Unbound: #'mockfn.mock-test/one-fn with args \[:unexpected\]"
            (mock :unexpected))))))

(deftest mock-call-count-test
  (let [definition {:function       'one-fn
                    :return-values  {[]            :no-args
                                     [:arg1]       :one-arg
                                     [:arg1 :arg2] :two-args}
                    :times-called   {[]            (atom 0)
                                     [:arg1]       (atom 0)
                                     [:arg1 :arg2] (atom 0)}
                    :times-expected {[]            [#(= 2 %)]
                                     [:arg1]       [#(= 1 %)]
                                     [:arg1 :arg2] [#(= 0 %)]}}
        mock       (mock/mock one-fn definition)]
    (testing "counts the number of times that each call was performed"
      (mock) (mock) (mock :arg1)
      (is (= 2 (-> mock meta (get-in [:times-called []]) deref)))
      (is (= 1 (-> mock meta (get-in [:times-called [:arg1]]) deref)))
      (is (= 0 (-> mock meta (get-in [:times-called [:arg1 :arg2]]) deref))))

    (testing "verifies that calls were performed the expected number of times"
      (is nil? (mock/verify mock))
      (mock :arg1 :arg2)
      (is (thrown-with-msg?
            ExceptionInfo #"Function one-fn unexpectedly called 1 times with arguments \[:arg1 :arg2\]"
            (mock/verify mock))))))
