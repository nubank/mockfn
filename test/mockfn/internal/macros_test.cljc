(ns mockfn.internal.macros-test
  (:require [clojure.test :refer [deftest testing is]]
            [mockfn.internal.macros :as internal.macros]
            [mockfn.fixtures :as mockfn.fixtures]))

(def specification
  (internal.macros/bindings->specification
    [['(mockfn.fixtures/one-fn) :one-fn-return]
     ['(mockfn.fixtures/one-fn :arg) :one-fn-arg-return]
     ['(mockfn.fixtures/other-fn) :other-fn-return 'call-count-matcher]]))

(deftest bindings->specification-test
  (testing "output includes every function in input"
    (is (= ['mockfn.fixtures/one-fn 'mockfn.fixtures/other-fn]
           (keys specification))))

  (testing "output includes functions"
    (is (= 'mockfn.fixtures/one-fn
           (get-in specification ['mockfn.fixtures/one-fn :stubbed/function])))
    (is (= 'mockfn.fixtures/other-fn
           (get-in specification ['mockfn.fixtures/other-fn :stubbed/function]))))

  (testing "output includes return values for each function and argument list"
    (is (= :one-fn-return
           (get-in specification ['mockfn.fixtures/one-fn :stubbed/calls [] :providing/return-value])))
    (is (= :one-fn-arg-return
           (get-in specification ['mockfn.fixtures/one-fn :stubbed/calls [:arg] :providing/return-value])))
    (is (= :other-fn-return
           (get-in specification ['mockfn.fixtures/other-fn :stubbed/calls [] :providing/return-value]))))

  (testing "output includes atoms for counting calls for each function and argument list"
    (is (= '(clojure.core/atom 0)
           (get-in specification ['mockfn.fixtures/other-fn :stubbed/calls [] :verifying/times-called]))))

  (testing "output includes the call-count matcher for each function and argument list"
    (is (nil? (get-in specification ['mockfn.fixtures/one-fn :stubbed/calls [] :verifying/times-expected])))
    (is (nil? (get-in specification ['mockfn.fixtures/one-fn :stubbed/calls [:arg] :verifying/times-expected])))
    (is (= 'call-count-matcher (get-in specification ['mockfn.fixtures/other-fn :stubbed/calls [] :verifying/times-expected])))))

(def redef-bindings
  (internal.macros/specification->redef-bindings specification))

(deftest specification->redef-bindings-test
  (testing "produces a binding for each function in specification"
    (is (= ['mockfn.fixtures/one-fn 'mockfn.fixtures/other-fn]
           (->> redef-bindings (partition 2) (map first))))

    (testing "produces a mock for each function in the specification"
      (is (= [`(mockfn.mock/mock mockfn.fixtures/one-fn ~('mockfn.fixtures/one-fn specification))
              `(mockfn.mock/mock mockfn.fixtures/other-fn ~('mockfn.fixtures/other-fn specification))]
             (->> redef-bindings (partition 2) (map last)))))))
