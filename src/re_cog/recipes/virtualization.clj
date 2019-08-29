(ns re-cog.recipes.virtualization
  "Setting up hypervisors"
  (:require
   [re-cog.facts.config :refer (configuration)]
   [re-cog.resources.file :refer (symlink directory chmod)]
   [re-cog.resources.user :refer (group-add)]
   [re-cog.common.functions :refer (require-functions require-resources)]
   [re-cog.common :refer (require-constants)]
   [re-cog.common.defs :refer (def-inline)]))

(require-functions)
(require-resources)
(require-constants)

(def-inline lxd
  "Installing lxd"
  []
  (letfn [(init []
            (fn []
              (script ("/usr/bin/lxd" "init" "--auto"))))]
    (let [{:keys [user]} (configuration)]
      (package "lxd" :present)
      (package "zfsutils-linux" :present)
      (group-add "libvirtd" user)
      (run (init)))))
