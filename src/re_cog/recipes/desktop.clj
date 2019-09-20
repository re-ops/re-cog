(ns re-cog.recipes.desktop
  "Desktop related setup"
  (:require
   [re-cog.resources.package :refer (package repository key-file fingerprint update-)]
   [re-cog.resources.download :refer (download)]
   [re-cog.common.functions :refer (require-functions require-resources)]
   [re-cog.common :refer (require-constants)]
   [re-cog.common.defs :refer (def-inline)]))

(require-functions)
(require-resources)
(require-constants)

(def-inline chrome
  "Google chrome setup"
  []
  (let [repo "deb [arch=amd64] http://dl.google.com/linux/chrome/deb/ stable main"
        url "https://dl-ssl.google.com/linux/linux_signing_key.pub"
        key "/tmp/linux_signing_key.pub"]
    (download url "/tmp/chrome-key" "")
    (key-file key)
    (fingerprint "7FAC5991")
    (repository repo :present)
    (update-)
    (package "google-chrome-stable" :present)))
