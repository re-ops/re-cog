(ns re-cog.recipes.tooling
  "Common tools"
  (:require
   [re-cog.resources.download :refer (download)]
   [re-cog.resources.file :refer (rename symlink chmod)]
   [re-cog.resources.archive :refer (untar bzip2)]
   [re-cog.common.functions :refer (require-functions require-resources)]
   [re-cog.common :refer (require-constants)]
   [re-cog.resources.permissions :refer (set-file-acl)]
   [re-cog.common.defs :refer (def-inline)]))

(require-functions)
(require-resources)
(require-constants)

(def-inline disk-tools
  "Common disk utilities"
  []
  (package "gt5" :present))

(def-inline security-tools
  "Common security tooling"
  []
  (package "pwgen" :present)
  (package "veracrypt" :present))

