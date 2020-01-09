(ns re-cog.recipes.node
  "Setting up nodejs and some utlities"
  (:require
   [re-cog.facts.config :refer (configuration)]
   [re-cog.resources.file :refer (symlink)]
   [re-cog.common.functions :refer (require-functions require-resources)]
   [re-cog.common :refer (require-constants)]
   [re-cog.common.defs :refer (def-inline)]))

(require-functions)
(require-resources)
(require-constants)

(def-inline install
  "Settimgn up nodejs"
  []
  (package "nodejs" :present)
  (package "npm" :present))

(def-inline {:depends #'re-cog.recipes.node/install} vuepress
  "Setting up vuepress"
  []
  (letfn [(install []
            (script ("/usr/bin/npm" "install" "vuepress")))]
    (let [{:keys [home]} (configuration)]
      (run install)
      (symlink  (<< "~{home}/bin/vuepress") (<< "~{home}/nodemodules/vuepress.js")))))
