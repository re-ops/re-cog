(ns re-cog.recipes.osquery
  (:require
   [re-cog.common :refer (require-functions def-inline)]
   [re-cog.resources.download :refer (download)]))

(require-functions)

(def-inline install
  "Installing osquery"
  [archive sum]
  (download (<< "https://pkg.osquery.io/deb/~{archive}") (<< "/tmp/~{archive}") sum))
