(ns re-cog.recipes.intellij
  "Setting up Intellij"
  (:require
   [re-cog.common.recipe :refer (require-recipe)]
   [re-cog.resources.download :refer (download)]
   [re-cog.resources.archive :refer (untar)]
   [re-cog.facts.config :refer (configuration)]
   [re-cog.resources.file :refer (file line)]
   [re-cog.resources.permissions :refer (set-file-acl)]))

(require-recipe)

(def-inline install
  "Setting up Intellij Idea community edition"
  []
  (let [{:keys [home]} (configuration)
        version "ideaIU-2019.3.1"
        tmp (<< "/tmp/~{version}.tar.gz")
        expected "87543537c524c6f67c88e1e2af3865bb233099bf405db2df131a66ebf4655532"
        url (<< "https://download-cf.jetbrains.com/idea/~{version}.tar.gz")]
    (download url tmp expected)
    (set-file-acl "re-ops" "rwX" "/opt/")
    (untar tmp "/opt/")
    (directory (<< "~{home}/bin/") :present)))

