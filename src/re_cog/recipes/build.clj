(ns re-cog.recipes.build
  (:require
   [re-cog.common.functions :refer (require-functions)]
   [re-cog.common :refer (def-inline)]
   [re-cog.resources.download :refer (download)]
   [re-cog.resources.package :refer (package)]
   [re-cog.resources.file :refer (symlink)]
   [re-cog.resources.archive :refer (unzip)]
   [re-cog.resources.permissions :refer (set-file-acl)]))

(require-functions)

(def-inline packer
  "Setting up://www.packer.io/"
  []
  (let [dest "/tmp/packer_1.3.1_linux_amd64.zip"
        sum "254cf648a638f7ebd37dc1b334abe940da30b30ac3465b6e0a9ad59829932fa3"
        url "https://releases.hashicorp.com/packer/1.3.1/packer_1.3.1_linux_amd64.zip"]
    (download url dest sum)
    (set-file-acl "re-ops" "rwX" "/opt")
    (unzip dest "/opt/")
    (set-file-acl "re-ops" "rwX" "/usr/local/bin/")
    (symlink "/usr/local/bin/packer" "/opt/packer")))


