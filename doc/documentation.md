# mockfn

[`mockfn`](https://github.com/pmatiello/mockfn) is a library supporting
mockist test-driven-development in Clojure. It is meant to be used
alongside a regular testing framework such as `clojure.test`.

## Basic Usage

In order to use `mockfn`, it's enough to require it in a test
namespace.

```clj
(:require [mockfn.core :refer :all]
          ...)
```

This will bring `mockfn` features in scope for this namespace.

### Stubbing Function Calls

```clj
(deftest providing-test
  (providing [(support-fn :argument) :result]
    (is (= :result (tested-fn :argument)))))
```

### Verifying Interactions

```clj
(deftest verifying-test
  (verifying [(support-fn :argument) :result (exactly 1)]
    (is (= :result (tested-fn :argument)))))
```

## Argument Matchers

```clj
(deftest argument-matcher-test
  (providing [(support-fn (at-least 10) (at-most 20)) 15]
    (is (= 15 (tested-fn 10 20)))))
```
