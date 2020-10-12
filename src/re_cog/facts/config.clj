(ns re-cog.facts.config
  (:require
   [clojure.core.strint :refer (<<)]
   [aero.core :refer (read-config)]
   [me.raynes.fs :refer (exists?)]))

(def root "/tmp/resources")

(defn configuration
  ([]
   {:pre []}
   (when-not (exists? root)
     (throw (ex-info (<< "missing configuration directory ~{root}") {})))
   (if (exists? (<< "~{root}/prod/secrets.edn"))
     (read-config (<< "~{root}/prod/config.edn"))
     (read-config (<< "~{root}/dev/config.edn"))))
  ([& ks]
   (get-in (configuration) ks)))
