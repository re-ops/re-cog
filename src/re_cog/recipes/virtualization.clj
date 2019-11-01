(ns re-cog.recipes.virtualization
  "Setting up hypervisors"
  (:require
   [re-cog.facts.config :refer (configuration)]
   [re-cog.resources.file :refer (symlink directory chmod)]
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
              (script ("sudo" "/usr/bin/lxd" "init" "--auto"))))]
    (let [{:keys [user]} (configuration)]
      (package "lxd" :present)
      (package "zfsutils-linux" :present)
      (run (init)))))

(def-inline kvm
  "Installing KVM"
  []
  (let [{:keys [user]} (configuration)]
    (package "qemu-kvm" :present)
    (package  "libvirt-bin" :present)
    (package "bridge-utils" :present)
    (package "virt-manager" :present)))
