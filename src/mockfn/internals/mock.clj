(ns mockfn.internals.mock)

(defn- throw-unexpected-call-received
  [func args]
  (throw (ex-info
           (format "Unexpected call to %s with args %s." func args) {})))

(defn- return-value-for
  [func spec args]
  (when-not (-> spec :return-values (contains? args))
    (throw-unexpected-call-received func args))
  (-> spec (get-in [:times-called args]) (swap! inc))
  (get-in spec [:return-values args]))

(defn mock [func spec]
  (with-meta
    (fn [& args] (return-value-for func spec (into [] args)))
    spec))

(defn- throw-unexpected-call-verified [function args times-called]
  (throw (ex-info (format "Function %s unexpectedly called %s times with arguments %s." function times-called args) {})))

(defn verify [mock]
  (doseq [args      (-> mock meta :times-expected keys)
          expected? (-> mock meta :times-expected (get args))]
    (let [times-called (-> mock meta :times-called (get args) deref)]
      (when-not (expected? times-called)
        (throw-unexpected-call-verified (-> mock meta :function) args times-called)))))
