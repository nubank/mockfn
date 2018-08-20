(ns mockfn.core
  (:require [mockfn.macros :as macros]
            [mockfn.matchers :as matchers]))

(def #^{:macro true} ^:deprecated providing #'macros/providing)
(def #^{:macro true} ^:deprecated verifying #'macros/verifying)

(def ^:deprecated exactly matchers/exactly)
(def ^:deprecated at-least matchers/at-least)
(def ^:deprecated at-most matchers/at-most)
(def ^:deprecated any matchers/any)
(def ^:deprecated a matchers/a)
