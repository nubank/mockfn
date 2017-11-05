(ns mockfn.internals.stub)

(defn- throw-unexpected-call
  [func args]
  (throw (ex-info (format "Unexpected call to %s with args %s" func args) {})))

(defn- return-value-for
  [func spec args]
  (when-not (-> spec :return-values (contains? args))
    (throw-unexpected-call func args))
  (-> spec (get-in [:times-called args]) (swap! inc))
  (get-in spec [:return-values args]))

(defn stub [func spec]
  (with-meta
    (fn [& args] (return-value-for func spec (into [] args)))
    spec))
