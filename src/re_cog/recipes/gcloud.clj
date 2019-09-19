(ns re-cog.recipes.gcloud
  (:require
   [re-cog.resources.package :refer (package key-file fingerprint update-)]
   [re-cog.resources.file :refer (file line)]
   [re-cog.resources.permissions :refer (set-file-acl)]
   [re-cog.resources.download :refer (download)]
   [re-cog.common.functions :refer (require-functions require-resources)]
   [re-cog.common :refer (require-constants)]
   [re-cog.common.defs :refer (def-inline)]))

(require-functions)
(require-resources)
(require-constants)

(def-inline install
  "Installing gcloud client"
  []
  (let [listing "/etc/apt/sources.list.d/google-cloud-sdk.list"
        key "cloud.google.gpg"
        keyrings "/usr/share/keyrings/"
        repo (<< "deb [signed-by=~{keyrings}/~{key}] http://packages.cloud.google.com/apt cloud-sdk main")         url "https://packages.cloud.google.com/apt/doc/apt-key.gpg"]
    (set-file-acl "re-ops" "rwX" keyrings)
    (download url (<< "~{keyrings}/~{key}") "1fe629470162c72777c1ed5e5b0f392acf403cf6a374cb229cf76109b5c90ed5")
    (key-file (<< "~{keyrings}/~{key}"))
    (file listing :present)
    (line listing repo :present)
    (update-)
    (package "google-cloud-sdk" :present)))

(def-inline {:depends #'re-cog.recipes.gcloud/install} emulators
  "Installing emulators (pubsub)"
  []
  (package "google-cloud-sdk-pubsub-emulator" :present))
