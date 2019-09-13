(ns re-cog.recipes.build
  (:require
   [re-cog.common.functions :refer (require-functions)]
   [re-cog.common.defs :refer (def-inline)]
   [re-cog.resources.download :refer (download)]
   [re-cog.resources.exec :refer (run)]
   [re-cog.resources.file :refer (symlink directory chmod)]
   [re-cog.resources.archive :refer (unzip)]
   [re-cog.resources.permissions :refer (set-file-acl)]))

(require-functions)

(def-inline packer
  "Setting up://www.packer.io/"
  []
  (let [version "1.4.3"
        dest (<< "/tmp/packer_~{version}_linux_amd64.zip")
        sum "c89367c7ccb50ca3fa10129bbbe89273fba0fa6a75b44e07692a32f92b1cbf55"
        url (<< "https://releases.hashicorp.com/packer/~{version}/packer_~{version}_linux_amd64.zip")]
    (download url dest sum)
    (set-file-acl "re-ops" "rwX" "/opt")
    (unzip dest "/opt/")
    (set-file-acl "re-ops" "rwX" "/usr/local/bin/")
    (symlink "/usr/local/bin/packer" "/opt/packer")))

(def-inline lein
  "Setting up https://leiningen.org/"
  []
  (let [home "/home/re-ops"
        dest (<< "~{home}/bin/lein")
        sum "32acacc8354627724d27231bed8fa190d7df0356972e2fd44ca144c084ad4fc7"
        url "https://raw.githubusercontent.com/technomancy/leiningen/stable/bin/lein"]
    (directory (<< "~{home}/bin") :present)
    (download url dest sum)
    (chmod dest "+x")))
