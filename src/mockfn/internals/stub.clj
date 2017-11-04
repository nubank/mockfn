(ns mockfn.internals.stub)

(defn- throw-unexpected-call
  [func args]
  (throw (ex-info (format "Unexpected call to %s with args %s" func args) {})))

(defn- return-value-for
  [func definition args]
  (when-not (-> definition :return-values (contains? args))
    (throw-unexpected-call func args))
  (-> definition (get-in [:times-called args]) (swap! inc))
  (get-in definition [:return-values args]))

(defn stub [func definition]
  (fn [& args] (return-value-for func definition (into [] args))))
