(ns re-cog.recipes.platformio
  "Setting up https://platformio.org"
  (:require
   [re-cog.resources.package :refer (package)]
   [re-cog.common.defs :refer (def-inline)]
   [re-cog.common.functions :refer (require-functions require-resources)]
   [re-cog.common :refer (require-constants)]))

(require-constants)
(require-functions)
(require-resources)

(def-inline core
  "Setting up platformio core package"
  []
  (letfn [(install []
            (script ("/usr/bin/pip3" "install" "-U" "platformio")))]
    (run install)))
