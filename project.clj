(defproject todomata "0.1.0-SNAPSHOT"
            :description "Todo application with calculation of short path for completing all tasks"
            :url "https://github.com/nvbn/todomata"
            :license {:name "Eclipse Public License"
                      :url "http://www.eclipse.org/legal/epl-v10.html"}
            :dependencies [[org.clojure/clojure "1.6.0"]
                           [org.clojure/clojurescript "0.0-2342"]
                           [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                           [org.clojure/tools.logging "0.3.1"]
                           [com.cognitect/transit-clj "0.8.259"]
                           [com.cognitect/transit-cljs "0.8.188"]
                           [compojure "1.1.9"]
                           [hiccup "1.0.5"]
                           [cljs-http "0.1.16"]
                           [garden "1.2.1"]
                           [ring "1.3.1"]
                           [swiss-arrows "1.0.0"]
                           [jayq "2.5.2"]
                           [om "0.7.3"]
                           [environ "1.0.0"]
                           [alandipert/storage-atom "1.2.3"]
                           [secretary "1.2.1"]
                           [sablono "0.2.22"]
                           [prismatic/om-tools "0.3.3"]
                           [ring-transit "0.1.2"]
                           [com.cemerick/friend "0.2.1"]
                           [friend-oauth2 "0.1.1"]
                           [com.novemberain/monger "2.0.0"]
                           [clj-time "0.8.0"]]
            :plugins [[lein-cljsbuild "1.0.3"]
                      [com.keminglabs/cljx "0.4.0"]
                      [lein-garden "0.2.1"]
                      [lein-environ "1.0.0"]
                      [lein-ring "0.8.11"]
                      [lein-ancient "0.5.5"]
                      [com.cemerick/clojurescript.test "0.3.1"]
                      [lein-bower "0.5.1"]]
            :profiles {:dev {:cljsbuild {:builds
                                         {:main {:source-paths ["src/cljs" "target/generated-cljs"]
                                                 :compiler {:output-to "resources/public/main.js"
                                                            :output-dir "resources/public/cljs-target"
                                                            :source-map true
                                                            :optimizations :none}}
                                          :test {:source-paths ["src/cljs" "test/cljs"
                                                                "target/generated-cljs"]
                                                 :compiler {:output-to "target/cljs-test.js"
                                                            :optimizations :whitespace
                                                            :pretty-print true}}}
                                         :test-commands {"test" ["phantomjs" :runner
                                                                 "resources/public/components/es5-shim/es5-shim.js"
                                                                 "resources/public/components/es5-shim/es5-sham.js"
                                                                 "resources/public/components/jquery/dist/jquery.js"
                                                                 "resources/public/components/bootstrap/dist/js/bootstrap.js"
                                                                 "resources/public/components/react/react-with-addons.js"
                                                                 "target/cljs-test.js"]}}
                             :env {:is-debug true
                                   :github-client-id "a0eecaa258cd128cb7cb"
                                   :github-client-secret "f7da4dcd94e1250d5c5f9a61509abe90017fc9a8"
                                   :github-client-domain "http://localhost:3000"
                                   :mongo-db "todomata_changes"}
                             :jvm-opts ["-Xss16m" "-XX:+TieredCompilation" "-XX:TieredStopAtLevel=1"]}
                       :production {:aot :all
                                    :cljsbuild {:builds [{:source-paths ["src/cljs" "target/generated-cljs"]
                                                          :compiler {:externs ["resources/public/components/jquery/dist/jquery.min.js"
                                                                               "resources/public/components/bootstrap/dist/js/bootstrap.min.js"
                                                                               "resources/public/components/react/react.min.js"]
                                                                     :output-to "resources/public/main.js"
                                                                     :optimizations :advanced
                                                                     :pretty-print false}
                                                          :jar true}]}
                                    :env {:is-debug false}
                                    :hooks [cljx.hooks
                                            leiningen.cljsbuild
                                            leiningen.garden]}}
            :source-paths ["src/clj"]
            :test-paths ["test/clj"]
            :cljx {:builds [{:source-paths ["src/cljx"]
                             :output-path "target/classes"
                             :rules :clj}
                            {:source-paths ["src/cljx"]
                             :output-path "target/generated-cljs"
                             :rules :cljs}]}
            :garden {:builds [{:source-paths ["src/clj"]
                               :stylesheet todomata.style/main
                               :compiler {:output-to "resources/public/main.css"}}]}
            :ring {:handler todomata.handlers/app
                   :init todomata.handlers/init}
            :bower {:directory "resources/public/components"}
            :bower-dependencies [["bootstrap" "3.2.0"]
                                 ["font-awesome" "4.2.0"]
                                 ["jquery" "2.1.1"]
                                 ["react" "0.11.2"]
                                 ["es5-shim" "4.0.3"]])
