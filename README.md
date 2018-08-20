# mockfn

[`mockfn`](https://github.com/pmatiello/mockfn) is a library supporting
mockist test-driven-development in Clojure. It is meant to be used
alongside a regular testing framework such as `clojure.test`.

[![Clojars Project](https://img.shields.io/clojars/v/mockfn.svg)](https://clojars.org/mockfn)

[![CircleCI](https://circleci.com/gh/pmatiello/mockfn.svg?style=svg)](https://circleci.com/gh/pmatiello/mockfn)

## Usage

The `providing` macro replaces a function with a configured mock.

```clj
(deftest providing-test
  (providing
    [(one-fn) :mocked]
    (is (= :mocked (one-fn)))))
```

The `verifying` macro works similarly, but also defines an expectation
for the number of times a call should be performed during the test.

```clj
(deftest verifying-test
  (verifying
    [(one-fn) :mocked (at-least 1)]
    (is (= :mocked (one-fn)))))
```

Refer to the [documentation](doc/documentation.md) for more detailed
information, including:

- [Framework-agonostic usage](#framework-agonostic-usage)
- [Syntax sugar for `clojure.test`](#syntax-sugar-for-clojuretest)
- [Argument matchers](#argument-matchers)

## License

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.

