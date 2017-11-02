(ns mockfn.internals.misc)

(defn- single-val-map-fn [f]
  (fn [[k v]] [k (f v)]))

(defn map-vals [f coll]
  (->> coll
       (map (single-val-map-fn f))
       (into {})))
