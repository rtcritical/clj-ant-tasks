(defproject rtcritical/clj-ant-tasks "1.0"
  :description "Easily run Apache Ant tasks in clojure."
  :url "https://github.com/rtcritical/clj-ant-tasks"
  :license {:name "The MIT License"
            :url "http://opensource.org/licenses/mit-license.php"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.clojure/data.xml "0.0.8"]
                 [org.apache.ant/ant "1.10.7"]]
  :aliases {"test-all" ["with-profile" "default:+1.7:+1.8:+1.9:+1.10" "test"]}
  :profiles
  {:1.7 {:dependencies [[org.clojure/clojure "1.7.0"]]}
   :1.8 {:dependencies [[org.clojure/clojure "1.8.0"]]}
   :1.9 {:dependencies [[org.clojure/clojure "1.9.0"]]}
   :1.10 {:dependencies [[org.clojure/clojure "1.10.0"]]}})
