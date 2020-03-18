# mockfn

[`mockfn`](https://github.com/pmatiello/mockfn) is a Clojure(script) library
supporting mockist test-driven-development in Clojure. It is meant to be used
alongside a regular testing framework such as `clojure.test`.

[![Clojars Project](https://img.shields.io/clojars/v/nubank/mockfn.svg)](https://clojars.org/nubank/mockfn)

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

- [Framework-agonostic usage](doc/documentation.md#framework-agonostic-usage)
- [Syntax sugar for `clojure.test`](doc/documentation.md#syntax-sugar-for-clojuretest)
- [Argument matchers](doc/documentation.md#argument-matchers)

## License

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.

