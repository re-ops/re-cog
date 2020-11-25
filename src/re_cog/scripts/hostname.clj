(ns re-cog.scripts.hostname
  (:require  [pallet.stevedore :refer (script)]))

(defn hostnamectl
  "sets hostname and hosts file"
  [hostname fqdn]
  (script
   ("sudo" "/usr/bin/hostnamectl" "set-hostname" ~hostname)))
