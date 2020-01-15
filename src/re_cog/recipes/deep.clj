(ns re-cog.recipes.deep
  "Deep learning related tools and libraries"
  (:require
   [re-cog.common :refer (require-constants)]
   [re-cog.resources.package :refer (package)]
   [re-cog.common.functions :refer (require-functions require-resources)]
   [re-cog.common.defs :refer (def-inline)]))

(require-functions)
(require-resources)
(require-constants)

(def-inline intel-mkl
  "Setting up intel-mkl required by neanderthal"
  []
  (letfn [(set-defaults []
            (script
             (pipe
              ("echo" "'libmkl-rt libmkl-rt/use-as-default-blas-lapack boolean true'")
              ("sudo" "debconf-set-selections"))
             (pipe
              ("echo" "'libmkl-rt libmkl-rt/exact-so-3-selections multiselect libblas.so.3,liblapack.so.3,libblas64.so.3,liblapack64.so.3,'")
              ("sudo" "debconf-set-selections"))
             (pipe
              ("echo" "'libmkl-dev libmkl-rt/exact-so-3-selections multiselect libblas.so.3,liblapack.so.3,libblas64.so.3,liblapack64.so.3,'")
              ("sudo" "debconf-set-selections"))
             (pipe
              ("echo" "'libmkl-dev:amd64 libmkl-rt/exact-so-3-selections multiselect libblas.so.3,liblapack.so.3,libblas64.so.3,liblapack64.so.3,'")
              ("sudo" "debconf-set-selections"))
             (pipe
              ("echo" "'libmkl-rt libmkl-rt/exact-so-3-selections multiselect libblas.so.3,liblapack.so.3,libblas64.so.3,liblapack64.so.3,'")
              ("sudo" "debconf-set-selections"))))]
    (package "debconf-utils" :present)
    (run set-defaults)
    (package "intel-mkl" :present)))
