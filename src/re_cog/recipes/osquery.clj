(ns re-cog.recipes.osquery
  (:require
   [re-cog.common.functions :refer (require-functions)]
   [re-cog.common :refer (require-constants def-inline)]
   [re-cog.resources.download :refer (download)]
   [re-cog.resources.permissions :refer (set-file-acl)]
   [re-cog.resources.file :refer (copy)]
   [re-cog.resources.package :refer (package)]))

(require-functions)
(require-constants)

(def-inline install
  "Installing osquery"
  []
  (let [archive "osquery_3.3.2_1.linux.amd64.deb"
        sum "6c54a55df1feaf410064bc87696f9829d0787fb5b9c7beabeb4f5cca7ce6c3fb"]
    (download (<< "https://pkg.osquery.io/deb/~{archive}") (<< "/tmp/~{archive}") sum)
    (package (<< "/tmp/~{archive}") :present)
    (set-file-acl "re-ops" "rwX" "/etc/osquery")
    (copy "/tmp/resources/osquery.conf" "/etc/osquery/osquery.conf")))
