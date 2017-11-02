(ns mockfn.core)

(defn stub [fn-name args->ret-val]
  (fn [& args]
    (if (contains? args->ret-val args)
      (get args->ret-val args)
      (throw (ex-info (format "Unexpected call to %s with args %s" fn-name args) {})))))

(defn- to-redefinition
  [[[function & args] ret-val]]
  [function `(stub ~function {(seq [~@args]) ~ret-val})])

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
