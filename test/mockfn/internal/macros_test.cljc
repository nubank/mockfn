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
           (get-in specification ['mockfn.fixtures/one-fn :function])))
    (is (= 'mockfn.fixtures/other-fn
           (get-in specification ['mockfn.fixtures/other-fn :function]))))

  (testing "output includes return values for each function and argument list"
    (is (= :one-fn-return
           (get-in specification ['mockfn.fixtures/one-fn :return-values []])))
    (is (= :one-fn-arg-return
           (get-in specification ['mockfn.fixtures/one-fn :return-values [:arg]])))
    (is (= :other-fn-return
           (get-in specification ['mockfn.fixtures/other-fn :return-values []]))))

  (testing "output includes atoms for counting calls for each function and argument list"
    (is (= '(clojure.core/atom 0)
           (get-in specification ['mockfn.fixtures/one-fn :times-called []])))
    (is (= '(clojure.core/atom 0)
           (get-in specification ['mockfn.fixtures/one-fn :times-called [:arg]])))
    (is (= '(clojure.core/atom 0)
           (get-in specification ['mockfn.fixtures/other-fn :times-called []]))))

  (testing "output includes the call-count matcher for each function and argument list"
    (is (= []
           (get-in specification ['mockfn.fixtures/one-fn :times-expected []])))
    (is (= []
           (get-in specification ['mockfn.fixtures/one-fn :times-expected [:arg]])))
    (is (= ['call-count-matcher]
           (get-in specification ['mockfn.fixtures/other-fn :times-expected []])))))

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
