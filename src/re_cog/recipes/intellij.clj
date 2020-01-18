(ns re-cog.recipes.intellij
  "Setting up Intellij"
  (:require
   [re-cog.common.recipe :refer (require-recipe)]
   [re-cog.resources.download :refer (download)]
   [re-cog.resources.archive :refer (untar)]
   [re-cog.facts.config :refer (configuration)]
   [re-cog.resources.file :refer (file line)]))

(require-recipe)

(def-inline {:depends #'re-cog.recipes.access/permissions} install
  "Setting up Intellij Idea community edition"
  []
  (let [{:keys [home]} (configuration)
        version "ideaIC-2019.3.1"
        tmp (<< "/tmp/~{version}.tar.gz")
        expected "b67cc055d7ab18b2a864d05956407ae1f910eb295e2a73e6a6aa813260930509"
        url (<< "https://download-cf.jetbrains.com/idea/~{version}.tar.gz")]
    (download url tmp expected)
    (untar tmp "/opt/")
    (directory (<< "~{home}/bin/") :present)))
