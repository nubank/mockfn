# mockfn

[`mockfn`](https://github.com/pmatiello/mockfn) is a library supporting
mockist test-driven-development in Clojure. It is meant to be used
alongside a regular testing framework such as `clojure.test`.

[![CircleCI](https://circleci.com/gh/pmatiello/mockfn.svg?style=svg)](https://circleci.com/gh/pmatiello/mockfn)

## Availability

`mockfn` is available from [Clojars](https://clojars.org/mockfn).

```clj
[mockfn "0.1.0-SNAPSHOT"]
```

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
information.

## License

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.

