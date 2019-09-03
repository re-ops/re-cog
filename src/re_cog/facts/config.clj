(ns re-cog.facts.config
  (:require
   [me.raynes.fs :refer (exists?)]))

(def conf-file "/tmp/resources/config.edn")

(defn configuration
  []
  {:pre [(exists? conf-file)]}
  (clojure.edn/read-string (slurp conf-file)))

