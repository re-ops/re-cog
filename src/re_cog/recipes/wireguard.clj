(ns re-cog.recipes.wireguard
  "Wireguard client/server setup"
  (:require
   [re-cog.facts.config :refer (configuration)]
   [re-cog.resources.file :refer (chmod template)]
   [re-cog.resources.package :refer (package repository update-)]
   [re-cog.resources.permissions :refer (set-file-acl)]
   [re-cog.common.functions :refer (require-functions require-resources)]
   [re-cog.common :refer (require-constants)]
   [re-cog.common.defs :refer (def-inline)]))

(require-functions)
(require-resources)
(require-constants)

(def-inline install
  "Setting up wireguard"
  []
  (repository "ppa:wireguard/wireguard" :present)
  (update-)
  (package "wireguard" :present))

(def-inline configure
  "Configure wireguard"
  []
  (let [private "/etc/wireguard/private-key"]
    (letfn [(private-key []
              (script
               ("/usr/bin/test" "-f" ~private)
               (when-not (= "$?" 0)
                 ("/usr/bin/wg" "genkey" ">" ~private))))]
      (run private-key)
      (let [pk (clojure.string/trim (slurp private))
            args {:ifc "eth1" :port "51820" :address "10.10.0.1/24" :private-key pk}]
        (set-file-acl "re-ops" "rwX" "/etc/wireguard/")
        (template "/tmp/resources/wireguard/wg0.conf.mustache" "/etc/wireguard/wg.conf" args)))))
