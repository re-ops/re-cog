(ns re-cog.scripts.nebula
  (:require
   [pallet.stevedore :refer (script)]
   [clojure.core.strint :refer (<<)]))

(defn sign [name ip groups crt key dest]
  (fn []
    (let [out-key (str dest "/" name ".key")
          out-crt (str dest "/" name ".crt")]
      (script
       ("/opt/nebula/nebula-cert" "sign" "-name" ~name "-ip" ~ip "-groups" ~groups "-ca-crt" ~crt "-ca-key" ~key "-out-key" ~out-key "-out-crt" ~out-crt)))))
