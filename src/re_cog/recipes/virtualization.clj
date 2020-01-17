(ns re-cog.recipes.virtualization
  "Setting up hypervisors"
  (:require
   [re-cog.resources.exec :refer [run]]
   [re-cog.common.recipe :refer (require-recipe)]
   [re-cog.facts.config :refer (configuration)]
   [re-cog.resources.file :refer (symlink directory chmod)]))

(require-recipe)

(def-inline lxd
  "Installing lxd"
  []
  (letfn [(init []
            (script ("sudo" "/usr/bin/lxd" "init" "--auto")))]
    (let [{:keys [user]} (configuration)]
      (package "lxd" :present)
      (package "zfsutils-linux" :present)
      (run init))))

(def-inline kvm
  "Installing KVM"
  []
  (let [{:keys [user]} (configuration)]
    (package "qemu-kvm" :present)
    (package  "libvirt-bin" :present)
    (package "bridge-utils" :present)
    (package "virt-manager" :present)))
