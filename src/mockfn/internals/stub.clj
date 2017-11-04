(ns mockfn.internals.stub)

(defn- throw-unexpected-call
  [func args]
  (throw (ex-info (format "Unexpected call to %s with args %s" func args) {})))

(defn- stubbed-for
  [func args args->ret-val counts]
  (swap! counts #(update % args (fnil inc 0)))
  (if (contains? args->ret-val args)
    (get args->ret-val args)
    (throw-unexpected-call func args)))

(defn stub [func args->ret-val]
  (let [counts (atom {})]
    (with-meta
      (fn [& args] (stubbed-for func (into [] args) args->ret-val counts))
      {:calls counts})))
