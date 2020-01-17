(ns re-cog.recipes.monitoring
  "monitoring tools"
  (:require
   [re-cog.common.recipe :refer (require-recipe)]
   [re-cog.resources.package :refer (package)]))

(require-recipe)

(def-inline system-monitoring
  "System monitoring tooling"
  []
  (package "sysstat" :present))
