# Change Log

All notable changes to this project are documented in this file.

## Releases

### 0.4.0 - UNRELEASED
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

## Planned

- Chained responses
