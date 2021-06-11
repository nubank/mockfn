(ns mockfn.internal.matchers
  (:require [mockfn.matchers :as matchers]))

(defn- matches-arg?
  "Given a matcher expected and an argument arg, verify whether there is a
  match.

  If expected is a plain value instead of an implementation of
  mockfn.matchers/Matcher, use a simple equality check to verify matching."
  [[expected arg]]
  (if (satisfies? matchers/Matcher expected)
    (matchers/matches? expected arg)
    (= expected arg)))

(defn matches-args?
  "Given a list of matchers expected and a list of values args, verify whether
  every value in args matches the corresponding (by position in sequence)
  matcher in expected."
  [expected args]
  (let [arity-matches?    (= (count expected) (count args))
        each-arg-matches? (every? matches-arg? (map vector expected args))]
    (or (= expected [matchers/any-args?])
        (and arity-matches? each-arg-matches?))))
