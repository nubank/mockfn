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

The `providing` macro replaces functions with mocks. These mocks return
preconfigured values when called with the expected arguments.

```clj
(testing "providing"
  (providing [(one-fn) :result]
    (is (= :result (one-fn)))))
```

As presented below, a mock (`one-fn`) can be configured with different
returns for different arguments.

```clj
(testing "providing - one function, different arguments"
  (providing [(one-fn :argument-1) :result-1
              (one-fn :argument-2) :result-2]
    (is (= :result-1 (one-fn :argument-1)))
    (is (= :result-2 (one-fn :argument-2)))))
```

It's also possible to configure multiple mocks, for multiple functions,
at once.

```clj
(testing "providing with more than one function"
  (providing [(one-fn :argument) :result-1
              (other-fn :argument) :result-2]
    (is (= :result-1 (one-fn :argument)))
    (is (= :result-2 (other-fn :argument))))))
```

### Verifying Interactions

The `verifying` macro works similarly, but also defines an expectation
for the number of times a call should be performed during the test. A
test will fail if this expectation is not met.

```clj
(testing "verifying"
  (verifying [(one-fn :argument) :result (exactly 1)]
    (is (= :result (one-fn :argument)))))
```

Notice that the expected number of calls is defined with a
[matcher](#built-in-matchers).

### Argument Matchers

Mocks can be configured to return a specific value for a range of
different arguments through [matchers](#built-in-matchers). 

```clj
(testing "argument matchers"
  (providing [(one-fn (at-least 10) (at-most 20)) 15]
    (is (= 15 (one-fn 12 18))))))
```

## Built-in Matchers

The following matchers are included in `mockfn`:

### exactly

```clj
(exactly expected)
```

Matches if actual value is equal to the expected value.

### at-least

```clj
(at-least expected)
```

Matches if actual value is greater or equal than the expected value.

### at-most

```clj
(at-most expected)
```

Matches if actual value is less or equal than the expected value.

### any

```clj
(any)
```

Always matches.


### a

```clj
(a expected)
```

Matches if actual value is an instance of the expected type.

## Quirks and Limitations

While `providing` and `verifying` calls can be nested, all required
stubs and expectations for a single mock must be defined in the same
call. Mocking a function in a inner `providing` or `verifying` call
will override any definitions made in the outer scope for the tests
being run in the inner scope.

```clj
(testing "nested mocks"
  (providing [(one-fn :argument-1) :result-1]
    (providing [(one-fn :argument-2) :result-2
                (other-fn :argument-3) :result-3]
      (is (thrown? ExceptionInfo (one-fn :argument-1)))
      (is (= :result-2 (one-fn :argument-2)))
      (is (= :result-3 (other-fn :argument-3))))
    (is (= :result-1 (one-fn :argument-1))))))
```
