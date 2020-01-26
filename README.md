# clj-ant-tasks

Run Apache ant tasks in Clojure. Very easy to use, and great way to leverage Apache ant's functionality where other libraries make things verbose and a PITA, i.e. zip and unzip archives.

Multiple ant tasks can be ran at once, allowing properties to be passed from one task to the next (i.e. first task might set a property, and the following task could use the property in it's execution).

## Installation

To install, add the following to your project `:dependencies`:

	[rtcritical/clj-ant-tasks "1.0"]

clj-ant-tasks tested against clojure 1.7, 1.8, 1.9, and 1.10.

## Usage

```clojure
user=> (require '[rtcritical.clj-ant-tasks :refer :all])
```

Two functions available:

1) run-ant-task
2) run-ant-tasks

They are very similar. Both use clojure.data.xml's sexp-to-element, so the syntax is hiccup-like.

Nested elements handled appropriately (all by clojure.data.xml).

Some examples below. More examples can be found in the project's test file.


## run-ant-task examples

Use #'run-ant-task to run a single ant task.


Touch a file

```clojure
user=> (run-ant-task :touch {:file "/tmp/test.txt"})
```


Zip a directory

```clojure
user=> (run-ant-task :zip {:destfile "/tmp/archive.zip" :basedir "/tmp/archive"})
```

Zip a directory, where the base directory is included in the archive

```clojure
user=> (run-ant-task :zip {:destfile "/tmp/archive.zip" :basedir "/tmp" :includes "archive/**"})
```




## run-ant-tasks examples

Use #'run-ant-tasks to run multiple tasks in sequence.


Set a property, then delete using the property

```clojure
user=> (run-ant-tasks [:property {:name "file.to.delete" :value "/tmp/test.txt"}] [:delete {:file "${file.to.delete}"}])
```


## Tests

To run the tests against Clojure 1.7, 1.8, and 1.9:

```console
	$ lein test-all
```

## License

Released under the MIT License: http://www.opensource.org/licenses/mit-license.php

