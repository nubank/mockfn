(ns mockfn.internals.stub-test
  (:require [clojure.test :refer :all]
            [mockfn.internals.stub :as stub])
  (:import (clojure.lang ExceptionInfo)))

(def one-fn)

(deftest stub-test
  (let [definition {:return-values {[]            :no-args
                                    [:arg1]       :one-arg
                                    [:arg1 :arg2] :two-args}
                    :times-called  {[]            (atom 0)
                                    [:arg1]       (atom 0)
                                    [:arg1 :arg2] (atom 0)}}
        stub       (stub/stub one-fn definition)]
    (testing "returns to expected calls with configured return values"
      (is (= :no-args (stub)))
      (is (= :one-arg (stub :arg1)))
      (is (= :two-args (stub :arg1 :arg2))))

    (testing "throws exception when called with unexpected arguments"
      (is (thrown-with-msg?
            ExceptionInfo #"Unexpected call to Unbound: #'mockfn.internals.stub-test/one-fn with args \[:unexpected\]"
            (stub :unexpected))))))

(deftest stub-call-count-test
  (let [definition {:return-values {[]            :no-args
                                    [:arg1]       :one-arg
                                    [:arg1 :arg2] :two-args}
                    :times-called  {[]            (atom 0)
                                    [:arg1]       (atom 0)
                                    [:arg1 :arg2] (atom 0)}}
        stub       (stub/stub one-fn definition)]
    (testing "counts the number of times that each call was performed"
      (stub) (stub) (stub :arg1)
      (is (= 2 (-> stub meta (get-in [:times-called []]) deref)))
      (is (= 1 (-> stub meta (get-in [:times-called [:arg1]]) deref)))
      (is (= 0 (-> stub meta (get-in [:times-called [:arg1 :arg2]]) deref))))))
