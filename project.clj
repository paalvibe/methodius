(defproject bmp "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/tools.cli "0.3.1"]
                 [cc.artifice/clj-ml "0.6.0"]]
  :main ^:skip-aot methodius.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
