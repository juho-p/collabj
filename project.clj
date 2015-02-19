(defproject collabj "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [
   [org.clojure/clojure "1.6.0"]
   [org.clojure/clojurescript "0.0-2644"]
   [http-kit "2.1.19"]
   [compojure "1.1.7"]
   [ring/ring-devel "1.3.2"]
   [hiccup "1.0.5"]
   [ring/ring-core "1.3.2"]
   [javax.servlet/servlet-api "2.5"]
   [hiccup "1.0.5"]
   [cheshire "5.4.0"]]
  :main collabj.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
