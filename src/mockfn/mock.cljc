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
    (doseq [[args stubbed-call] (:stubbed/calls spec)]
      (let [matcher      (:verifying/times-expected stubbed-call)
            times-called @(:verifying/times-called stubbed-call)]
        (when-not (matchers/matches? matcher times-called)
          (throw (ex-info (internal.mock/matcher-failure-ex-msg (-> spec :stubbed/function) args matcher times-called) {})))))))
