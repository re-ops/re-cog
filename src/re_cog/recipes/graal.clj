(ns re-cog.recipes.graal
  "Graal setup"
  (:require
   [re-cog.resources.download :refer (download)]
   [re-cog.resources.file :refer (rename symlink chmod)]
   [re-cog.resources.archive :refer (untar)]
   [re-cog.common.functions :refer (require-functions require-resources)]
   [re-cog.common :refer (require-constants)]
   [re-cog.resources.permissions :refer (set-file-acl)]
   [re-cog.common.defs :refer (def-inline)]))

(require-functions)
(require-resources)
(require-constants)

(def-inline install
  "Setting up Graal"
  []
  (let [version "19.2.0"
        release (<< "graalvm-ce-linux-amd64-~{version}.tar.gz")
        tmp (<< "/tmp/~{release}")
        expected "9d8a82788c3aaede4a05366f79f8b0b328957d0bb7479c986f6f1354b1c7c4ea"
        url (<< "https://github.com/oracle/graal/releases/download/vm-~{version}/~{release}.tar.gz")]
    (download url tmp expected)
    (set-file-acl "re-ops" "rwX" "/opt/")
    (untar tmp (<< "/opt/~{release}"))))
