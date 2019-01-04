(ns mockfn.parser
  (:require [mockfn.matchers :as matchers]))

(extend-type clojure.lang.Fn
  matchers/Matcher
  (matches? [this actual]
    (matchers/matches? (matchers/pred this) actual))
  (description [this]
    (matchers/description (matchers/pred this))))
