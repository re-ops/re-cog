(ns re-cog.facts.config
  (:require
   [aero.core :refer (read-config)]
   [me.raynes.fs :refer (exists?)]))

(def dev-conf "/tmp/resources/config.edn")
(def tmp-conf "resources/config.edn")

(defn configuration
  ([]
   {:pre [(or (exists? dev-conf) (exists? tmp-conf))]}
   (cond
     (exists? tmp-conf) (read-config tmp-conf)
     (exists? dev-conf) (read-config dev-conf)))
  ([& ks]
   (get-in (configuration) ks)))
