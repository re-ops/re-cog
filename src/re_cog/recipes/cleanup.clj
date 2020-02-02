(ns re-cog.recipes.cleanup
  (:require
   [re-cog.facts.query :refer (desktop?)]
   [re-cog.resources.file :refer (directory)]
   [re-cog.common.recipe :refer (require-recipe)]
   [re-cog.facts.config :refer (configuration)]))

(require-recipe)

(def-inline purge-folders
  "Clearing un-used folders in ~"
  []
  (let [{:keys [home]} (configuration)]
    (if (desktop?)
      (doseq [lib ["Music" "Pictures" "Public" "Templates" "Videos"]]
        (directory (<< "~{home}/~{lib}") :absent))
      {})))
