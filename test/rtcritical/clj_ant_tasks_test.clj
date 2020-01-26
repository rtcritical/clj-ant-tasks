(ns rtcritical.clj-ant-tasks-test
  (:require [clojure.java.io :as io]
            ;; [clojure.string :as str]
            [clojure.test :refer :all]
            [rtcritical.clj-ant-tasks :refer :all]))

;;; Utility Functions

(defn random-alphanumeric-str
  "Creates random string containing 0-9, a-z, A-Z"
  ([] (random-alphanumeric-str 8))
  ([n]
   (let [chars (map char (concat (range 48 57) (range 65 90) (range 97 122)))
         random-chars (take n (repeatedly #(rand-nth chars)))]
     (reduce str random-chars))))

(defn tmpdir []
  (System/getProperty "java.io.tmpdir"))

(defn make-tmp-file-name [& {:keys [prefix suffix directory-str]
                             :or {prefix "rtcritical"
                                  directory-str (tmpdir)}}]
  (let [separator (java.io.File/separator)]
    (str directory-str (if-not (.endsWith directory-str separator) separator)
         prefix (random-alphanumeric-str 12) suffix)))

(defn make-tmp-file
  "Creates a temp file, returning a java.io.File object. 'directory-file' param will override 'directory-str' param if both given."
  [& {:keys [prefix suffix directory-str directory-file]
      :or {prefix "rtcritical" ; must be at least 3 characters long
           directory-str (tmpdir)
           directory-file (io/file directory-str)}}]
  (java.io.File/createTempFile prefix suffix directory-file))

(defn make-tmp-dir
  [& {:keys [prefix parent-directory-str]
      :or {prefix "rtcritical"
           parent-directory-str (tmpdir)}}]
  (let [pathname (str parent-directory-str (java.io.File/separator) prefix
                      (random-alphanumeric-str 12))
        dir (io/file pathname)]
    (if (.mkdirs dir)
      dir
      (throw (Exception. "tmp directory not created successfully")))))

(defn make-tmp-file-name []
  (str (System/getProperty "java.io.tmpdir") (java.io.File/separator)
       "rtcritical-" (random-alphanumeric-str 12)))




;;; TESTS


(deftest test-run-ant-task-with-attribute
  (let [tf (make-tmp-file-name)]
    (try
      (run-ant-task :touch {:file tf})
      (is (.exists (io/file tf)))
      (finally
        (.delete (io/file tf))))))

(deftest test-run-ant-task-with-attributes-and-elements
  (let [td (make-tmp-dir)]
    (try
      (is (.exists (io/file td)))
      (run-ant-task :delete {:includeEmptyDirs "true"} [:fileset {:dir (.getName td)}])
      (is (false? (.exists (io/file td))))
      (finally
        (.delete (io/file td))))))

(deftest test-run-ant-task-with-no-attributes-and-elements
  (let [td (make-tmp-dir)
        tf (make-tmp-file :directory-file td)]
    (try
      (is (.exists (io/file td)))
      (is (.exists (io/file tf)))
      (run-ant-task :delete [:fileset {:dir (.getName td)}])
      (is (.exists (io/file td)))
      (is (false? (.exists (io/file tf))))
      (finally
        (.delete (io/file tf))
        (.delete (io/file td))))))

(deftest test-run-ant-task-with-nested-elements
  (let [td (make-tmp-dir)
        tf (make-tmp-file :directory-file td)
        tf-java (make-tmp-file :directory-file td :suffix ".java")]
    (try
      (is (.exists (io/file td)))
      (is (.exists (io/file tf)))
      (is (.exists (io/file tf-java)))
      (run-ant-task :delete [:fileset {:dir (.getName td)}
                         [:not [:filename {:name "**/*.java"}]]])
      (is (.exists (io/file td)))
      (is (false? (.exists (io/file tf))))
      (is (.exists (io/file tf-java)))
      (finally
        (.delete (io/file tf))
        (.delete (io/file tf-java))
        (.delete (io/file td))))))

(deftest test-run-ant-tasks-single-task
  (let [tf (make-tmp-file-name)]
    (try
      (is (false? (.exists (io/file tf))))
      (run-ant-tasks [:touch {:file tf}])
      (is (.exists (io/file tf)))
      (finally
        (.delete (io/file tf))))))

(deftest test-run-ant-tasks-multiple-tasks-with-attributes
  (let [tf1 (make-tmp-file-name)
        tf2 (make-tmp-file-name)]
    (try
      (is (false? (.exists (io/file tf1))))
      (is (false? (.exists (io/file tf2))))
      (run-ant-tasks [:touch {:file tf1}]
                     [:touch {:file tf2}])
      (is (.exists (io/file tf1)))
      (is (.exists (io/file tf2)))
      (finally
        (.delete (io/file tf1))
        (.delete (io/file tf2))))))

(deftest test-run-ant-tasks-property-for-subsequent-task-use
  (let [tf1 (make-tmp-file-name)]
    (try
      (is (false? (.exists (io/file tf1))))
      (run-ant-tasks [:property {:name "touch.file"
                                 :value tf1}]
                     [:touch {:file "${touch.file}"}])
      (is (.exists (io/file tf1)))
      (finally
        (.delete (io/file tf1))))))
