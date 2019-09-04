(ns mockfn.mock
  (:require [mockfn.internal.mock :as internal.mock]
            [mockfn.matchers :as matchers]
            [mockfn.parser]))

(def calling
  internal.mock/->Calling)

(def calling-original
  internal.mock/->CallingOriginal)

(defn mock [func spec]
  (with-meta
    (fn [& args] (internal.mock/call->ret-val func spec (into [] args)))
    spec))

(defn verify [mock]
  (let [spec (internal.mock/mock->spec mock)]
    (doseq [args    (-> spec :times-expected keys)
            matcher (-> spec :times-expected (get args))]
      (let [times-called (-> spec :times-called (get args) deref)]
        (when-not (matchers/matches? matcher times-called)
          (throw (ex-info (internal.mock/matcher-failure-ex-msg (-> spec :function) args matcher times-called) {})))))))
