(ns re-cog.recipes.python
  "Installing latest python versions"
  (:require
   [re-cog.common.defs :refer (def-inline)]
   [re-cog.common.functions :refer (require-functions require-resources)]
   [re-cog.common :refer (require-constants)]
   [re-cog.resources.package :refer (package repository update-)]))

(require-functions)
(require-resources)
(require-constants)

(def-inline python-3.7
  "Setting up python 3.7"
  []
  (repository "ppa:deadsnakes/ppa" :present)
  (update-)
  (package "python3.7" :present))
