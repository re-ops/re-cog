(ns re-cog.facts.config)

(defn configuration []
  (clojure.edn/read-string (slurp "/tmp/resources/config.edn")))

