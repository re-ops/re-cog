(ns re-cog.recipes.platformio
  "Setting up https://platformio.org"
  (:require
   [re-cog.resources.exec :refer [run]]
   [re-cog.common.recipe :refer (require-recipe)]
   [re-cog.resources.package :refer (package)]))

(require-recipe)

(def-inline core
  "Setting up platformio core package"
  []
  (letfn [(install []
            (script ("/usr/bin/pip3" "install" "-U" "platformio")))]
    (run install)))
