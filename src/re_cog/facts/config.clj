(ns re-cog.facts.config
  (:require
   [clojure.core.strint :refer (<<)]
   [aero.core :refer (read-config)]
   [me.raynes.fs :refer (exists?)]))

(def root "/tmp/resources/")

(defn configuration
  ([]
   {:pre [(exists? root)]}
   (letfn [(profile []
             (if (exists? (<< "~{root}/prod/secrets.edn")) :prod :dev))]
     (read-config (<< "~{root}/config.edn") {:profile (profile)})))
  ([& ks]
   (get-in (configuration) ks)))
