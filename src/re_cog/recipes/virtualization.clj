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
    (package "lxd" :present)
    (package "zfsutils-linux" :present)
    (run init)))

(def-inline kvm
  "Installing KVM"
  []
  (let [version (ubuntu-version)]
    (cond
      (<= version 18.04) (package "libvirt-bin" :present)
      (>= version 18.10) (do
                           (package "libvirt-daemon-system" :present)
                           (package "libvirt-clients" :present)))
    (package "qemu-kvm" :present)
    (package "bridge-utils" :present)
    (if (ubuntu-desktop?)
      (package "virt-manager" :present))))
