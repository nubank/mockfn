(ns mockfn.core-test
  (:require [clojure.test :refer :all]
            [mockfn.core :refer :all])
  (:import (clojure.lang ExceptionInfo)))

(def one-fn)

(deftest providing-test
  (providing
    [(one-fn) :mocked]
    (is (= :mocked (one-fn)))))

(deftest verifying-test
  (verifying
    [(one-fn) :mocked (exactly 1)]
    (is (= :mocked (one-fn)))))
