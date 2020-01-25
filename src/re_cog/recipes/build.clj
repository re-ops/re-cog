(ns re-cog.recipes.build
  "Build tools"
  (:require
   [re-cog.common.recipe :refer (require-recipe)]
   [re-cog.resources.download :refer (download)]
   [re-cog.resources.exec :refer (run)]
   [re-cog.resources.file :refer (symlink directory chmod)]
   [re-cog.resources.archive :refer (unzip)]))

(require-recipe)

(def-inline {:depends #'re-cog.recipes.access/permissions} packer
  "Setting up://www.packer.io/"
  []
  (let [version "1.4.3"
        dest (<< "/tmp/packer_~{version}_linux_amd64.zip")
        sum "c89367c7ccb50ca3fa10129bbbe89273fba0fa6a75b44e07692a32f92b1cbf55"
        url (<< "https://releases.hashicorp.com/packer/~{version}/packer_~{version}_linux_amd64.zip")]
    (download url dest sum)
    (unzip dest "/opt/")
    (symlink "/usr/local/bin/packer" "/opt/packer")))

(def-inline lein
  "Setting up https://leiningen.org/"
  []
  (let [{:keys [home user]} (configuration)
        dest (<< "~{home}/bin/lein")
        sum "32acacc8354627724d27231bed8fa190d7df0356972e2fd44ca144c084ad4fc7"
        url "https://raw.githubusercontent.com/technomancy/leiningen/stable/bin/lein"]
    (directory (<< "~{home}/bin") :present)
    (download url dest sum)
    (chmod dest "+x")))
