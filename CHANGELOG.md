# Change Log

All notable changes to this project are documented in this file.

### 0.6.1 - 2020-04-15
- `verifying` now supports anon functions as args [#4](https://github.com/nubank/mockfn/pull/4),[#5](https://github.com/nubank/mockfn/pull/5)

### 0.6.0 - 2020-03-18
- `verifying` now returns the return value of the body (like `providing` does)

### 0.5.0 - 2019-09-04
- Support for mocking private functions.

### 0.4.0 - 2019-03-13
- Clojurescript support
- Allow mocks to call the original function implementation.

### 0.3.0 - 2019-01-25
- Allow mocks to not only return base values, but invoke function calls.
  This enables mocked functions to throw exceptions.
- Allow for use of predicates to match function arguments

### 0.2.0 - 2018-08-20
- Added syntax sugar for clojure.test.
- Deprecated everything in the `mockfn.core` namespace.

### 0.1.0 - 2018-03-18
- Added function stubs.
- Added call verification.
- Added argument matchers.
