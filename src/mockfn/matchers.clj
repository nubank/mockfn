(ns mockfn.matchers)

(defn exactly [expected]
  #(= expected %))
