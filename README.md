# mockfn

A library for mocking Clojure functions.

## Usage

Mockfn is available from Clojars.

```clj
[mockfn "0.1.0-SNAPSHOT"]
```

## Examples

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
