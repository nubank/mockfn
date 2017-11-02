(ns mockfn.internals.stub)

(defn stub [fn-name args->ret-val]
  (fn [& args]
    (if (contains? args->ret-val (into [] args))
      (get args->ret-val (into [] args))
      (throw (ex-info (format "Unexpected call to %s with args %s" fn-name args) {})))))
