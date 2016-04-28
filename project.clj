(defproject bmp "0.1.0-SNAPSHOT"
  :repositories {"my.datomic.com" {:url "https://my.datomic.com/repo"
                                 :creds :gpg}}
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/tools.cli "0.3.1"]
                 [com.datomic/datomic-pro "0.9.5359"]
                 [cc.artifice/clj-ml "0.6.0"]]
  :main ^:skip-aot methodius.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
