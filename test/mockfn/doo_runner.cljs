(ns mockfn.doo-runner
  (:require [doo.runner :refer-macros [doo-tests]]
            mockfn.examples.basic-usage
            mockfn.macros-test
            mockfn.matchers-test
            mockfn.mock-test))

(enable-console-print!)

(doo-tests 'mockfn.examples.basic-usage
           'mockfn.macros-test
           'mockfn.matchers-test
           'mockfn.mock-test)
