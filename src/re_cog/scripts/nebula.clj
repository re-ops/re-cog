(ns re-cog.scripts.nebula
  (:require
   [pallet.stevedore :refer (script)]
   [clojure.core.strint :refer (<<)]))

(defn sign [name ip groups]
  (fn []
    (script
     ("/opt/nebula/nebula-cert" "sign" "-name" ~name "-ip" ~ip "-groups" ~groups))))
