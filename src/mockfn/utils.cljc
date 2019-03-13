(ns mockfn.utils
  #?(:cljs
     (:require
       [goog.string :as gstring]
       [goog.string.format])))

(def formatted
  #?(:clj format
     :cljs gstring/format))

