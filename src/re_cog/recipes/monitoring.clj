(ns re-cog.recipes.monitoring
  "monitoring tools"
  (:require
   [re-cog.common.functions :refer (require-functions require-resources)]
   [re-cog.common :refer (require-constants)]
   [re-cog.common.defs :refer (def-inline)]))

(require-functions)
(require-resources)
(require-constants)

(def-inline system-monitoring
  "System monitoring tooling"
  []
  (package "sysstat" :present))
