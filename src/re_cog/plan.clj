(ns re-cog.plan
  "Execution plans support")

(def base-machine
  {:osquery ['re-cog.recipes.osquery/install]
   :build ['re-cog.recipes.build/packer]})
