(ns mockfn.core)

(defn- to-redefinition
  [[[function & args] return-value]]
  [function `(constantly ~return-value)])

(defn- redefinitions
  [bindings]
  (->> bindings
       (partition 2)
       (map to-redefinition)
       (apply concat)))

(defmacro providing
  "I don't do a whole lot."
  [bindings & body]
  `(with-redefs ~(redefinitions bindings)
     (do ~@body)))
