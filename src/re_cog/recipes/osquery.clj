(ns re-cog.recipes.osquery
  (:require
   [re-cog.common :refer (require-functions require-constants def-inline)]
   [re-cog.resources.download :refer (download)]
   [re-cog.resources.file :refer (copy)]
   [re-cog.resources.package :refer (package)]))

(require-functions)
(require-constants)

(def-inline install
  "Installing osquery"
  [archive sum]
  (download (<< "https://pkg.osquery.io/deb/~{archive}") (<< "/tmp/~{archive}") sum)
  (package (<< "/tmp/~{archive}") :present)
  (copy "/tmp/resources/osquery.conf" "/etc/osquery/osquery.conf"))
