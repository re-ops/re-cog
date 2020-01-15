(ns re-cog.recipes.python
  "Installing latest python versions"
  (:require
   [re-cog.common.defs :refer (def-inline)]
   [re-cog.common.functions :refer (require-functions require-resources)]
   [re-cog.common :refer (require-constants)]
   [re-cog.resources.package :refer (package repository update-)]))

(require-constants)
(require-functions)
(require-resources)

(def-inline python-3.7
  "Setting up python 3.7"
  []
  (repository "ppa:deadsnakes/ppa" :present)
  (update-)
  (package "python3.7" :present))

(def-inline python-3
  "Installing system python 3"
  []
  (package "python3" :present)
  (package "python3-pip" :present))
