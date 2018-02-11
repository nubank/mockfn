(ns mockfn.core-test
  (:require [clojure.test :refer :all]
            [mockfn.core :refer :all]))

(def one-fn)

(deftest providing-test
  (providing
    [(one-fn) :mocked]
    (is (= :mocked (one-fn)))))

(deftest verifying-test
  (verifying
    [(one-fn) :mocked (at-least 1)]
    (is (= :mocked (one-fn)))))
