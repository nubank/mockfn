(defproject nubank/mockfn "0.6.1"
  :description "A library for mocking Clojure functions."
  :url "https://github.com/pmatiello/mockfn"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.10.1"]]

  :repositories [["publish" {:url "https://clojars.org/repo"
                             :username :env/clojars_username
                             :password :env/clojars_passwd
                             :sign-releases false}]]

  :plugins [[lein-project-version "0.1.0"]]

  :profiles {:dev {:plugins [[lein-cljsbuild "1.1.7"]
                             [lein-doo "0.1.11"]]
                   :dependencies [[org.clojure/clojurescript "1.10.520"]
                                  [org.clojure/core.rrb-vector "0.1.1"]]}}

  :aliases {"test-phantom" ["doo" "phantom" "test"]
            "test-node" ["doo" "node" "node-test" "once"]
            "test-node-watch" ["doo" "node" "node-test"]}
  ;; Below, :process-shim false is workaround for <https://github.com/bensu/doo/pull/141>
  :cljsbuild {:builds [{:id "test"
                        :source-paths ["src/" "test/"]
                        :compiler {:output-to "target/out/test.js"
                                   :output-dir "target/out"
                                   :main mockfn.doo-runner
                                   :optimizations :none
                                   :process-shim false}}
                       ;; Node.js requires :target :nodejs, hence the separate
                       ;; build configuration.
                       {:id "node-test"
                        :source-paths ["src/" "test/"]
                        :compiler {:output-to "target/node_out/test.js"
                                   :output-dir "target/node_out"
                                   :main mockfn.doo-runner
                                   :optimizations :none
                                   :target :nodejs
                                   :process-shim false}}]})
