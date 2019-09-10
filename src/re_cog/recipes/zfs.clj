(ns re-cog.recipes.zfs
  (:require
   [re-cog.resources.package :refer (package)]
   [re-cog.common.functions :refer (require-functions require-resources)]
   [re-cog.common :refer (require-constants)]
   [re-cog.common.defs :refer (def-inline)]))

(require-functions)
(require-resources)
(require-constants)

(def-inline zfs
  "Base zfs setup and tunning"
  []
  (package "zfsutils-linux" :present))
