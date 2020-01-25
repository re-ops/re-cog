(ns re-cog.recipes.3dprint
  (:require
   [re-cog.resources.exec :refer [run]]
   [re-cog.common.recipe :refer (require-recipe)]
   [clojure.core.strint :refer (<<)]
   [re-cog.resources.package :refer (package repository update-)]))

(require-recipe)

(def-inline cad
  "Cad tools"
  []
  (package "openscad" :present))

(def-inline slicing
  "Slicing tools"
  []
  (repository "ppa:thopiekar/cura" :present)
  (update-)
  (package "cura" :present))
