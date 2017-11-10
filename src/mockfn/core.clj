(ns mockfn.core
  (:require [mockfn.macros :as macros]
            [mockfn.matchers :as matchers]))

(def #^{:macro true} providing #'macros/providing)
(def #^{:macro true} verifying #'macros/verifying)

(def exactly matchers/exactly)
(def at-least matchers/at-least)
(def at-most matchers/at-most)
