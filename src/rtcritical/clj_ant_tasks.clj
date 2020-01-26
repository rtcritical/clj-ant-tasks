(ns rtcritical.clj-ant-tasks
  (:require [clojure.java.io :as io]
            [clojure.data.xml :as xml])
  (:import [org.apache.tools.ant Project ProjectHelper Target]))

;; Note: Not for high frequency use, as writes a build.xml file to the OS temp file system, and
;;       deletes when finished.

;; TODO: Find a way to re-implement without saving a build.xml file to the filesystem

(def target-name "rtcritical")

(defn- make-build-file [tasks]
  (let [f (java.io.File/createTempFile "rtcritical" ".xml"
                                       (io/file (System/getProperty "java.io.tmpdir")))]
    (with-open [fw (io/writer f)]
      (let [tags (xml/sexp-as-element
                  [:project {}
                   [:target {:name target-name}
                    tasks]])]
        (xml/emit tags fw)))
    f))

;; http://www.srccodes.com/p/article/9/Invoke-and-Execute-Hello-World-Ant-Script-Programmatically-using-Java-Code
(defn run-ant-tasks
  "Takes unlimited number of vectors of xml sexp representing ant tasks, as wanted by clojure.data.xml's #'sexp-as-element. Example: (run-ant-tasks [:echo \"hi\"] [:touch {:file \"/tmp/test.txt\"}])"
  [& rest]
  (let [build-file (make-build-file rest)]
    (try
      (let [project (doto (Project.)
                      (.setUserProperty "ant.file" (.getAbsolutePath build-file))
                      (.fireBuildStarted)
                      (.init))
            project-helper (ProjectHelper/getProjectHelper)]
        (.addReference project "ant.projectHelper" project-helper)
        (.parse project-helper project build-file)
        (doto project
          (.executeTarget target-name)
          (.fireBuildFinished nil))
        nil)
      (finally
        (.delete build-file)))))

(defn run-ant-task [& rest]
  (run-ant-tasks (vec rest)))


