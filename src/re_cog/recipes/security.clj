(ns re-cog.recipes.security
  "Common tools"
  (:require
   [re-cog.resources.download :refer (download)]
   [re-cog.common.functions :refer (require-functions require-resources)]
   [re-cog.common :refer (require-constants)]
   [re-cog.resources.permissions :refer (set-file-acl)]
   [re-cog.common.defs :refer (def-inline)]))

(require-functions)
(require-resources)
(require-constants)

(def-inline password
  "Password generation"
  []
  (package "pwgen" :present))

(def-inline veracrypt
  "Instaling veracrypt"
  []
  (let [version "1.24-Update3"
        url-version (clojure.string/lower-case version)
        deb (<< "veracrypt-console-~{version}-Ubuntu-18.04-amd64.deb")
        url (<< "https://launchpad.net/veracrypt/trunk/~{url-version}/+download/~{deb}")
        sum "e753503de072960f66416eb202a772869589e763135d631ab43d07d7c64550c3"]
    (download url (<< "/tmp/~{deb}") sum)
    (package (<< "/tmp/~{deb}") :present)))