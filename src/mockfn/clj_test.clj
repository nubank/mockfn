(ns mockfn.clj-test
  (:require [clojure.test :as test]
            [mockfn.macros :as macros]))

(declare providing)
(declare verifying)

(defn- only?
  [symbol form]
  (and (seq? form)
       (-> form first resolve #{symbol})))

(def ^:private providing-only? (partial only? #'providing))
(def ^:private verifying-only? (partial only? #'verifying))

(def ^:private removed ::removed)

(defn- mocking-form?
  [form]
  (or (providing-only? form) (verifying-only? form)))

(defn- collect-bindings
  "Collect providing/verifying binding bodies from `forms`, including those
  nested inside other sequential forms (e.g. `let` inside `testing`)."
  [pred forms]
  (letfn [(walk [form]
            (cond
              (pred form) (rest form)
              (sequential? form) (mapcat walk form)
              :else nil))]
    (vec (mapcat walk forms))))

(defn- strip-mocking-forms
  "Remove providing/verifying forms from `forms` while preserving surrounding
  structure (so a `let` that only wrapped mocks still remains valid)."
  [forms]
  (letfn [(strip [form]
            (cond
              (mocking-form? form)
              removed

              (list? form)
              (let [xs (remove #{removed} (map strip form))]
                (apply list xs))

              (vector? form)
              (into [] (remove #{removed} (map strip form)))

              (seq? form)
              (doall (remove #{removed} (map strip form)))

              :else
              form))]
    (->> forms
         (map strip)
         (remove #{removed})
         vec)))

(defmacro with-mocking
  [base-macro name & body]
  (let [providing-bindings (collect-bindings providing-only? body)
        verifying-bindings (collect-bindings verifying-only? body)
        actual-body        (strip-mocking-forms body)]
    `(~base-macro
       ~name
       (macros/providing [~@providing-bindings]
         (macros/verifying [~@verifying-bindings]
           ~@actual-body)))))

(defmacro deftest
  [name & body]
  `(with-mocking test/deftest ~name ~@body))

(defmacro testing
  [string & body]
  `(with-mocking test/testing ~string ~@body))
