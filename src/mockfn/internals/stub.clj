(ns mockfn.internals.stub)

(defn- throw-unexpected-call
  [fn-name args]
  (throw (ex-info (format "Unexpected call to %s with args %s" fn-name args) {})))

(defn- stubbed-for
  [fn-name args args->ret-val]
  (if (contains? args->ret-val args)
    (get args->ret-val args)
    (throw-unexpected-call fn-name args)))

(defn stub [fn-name args->ret-val]
  (fn [& args] (stubbed-for fn-name (into [] args) args->ret-val)))
