(ns re-cog.recipes.docker
  (:require
   [clojure.core.strint :refer (<<)]
   [re-cog.resources.file :refer (file line)]
   [re-cog.resources.package :refer (package key-file update-)]
   [re-cog.resources.user :refer (group-add)]
   [re-cog.resources.download :refer (download)]
   [re-cog.resources.permissions :refer (set-file-acl)]
   [re-cog.common.functions :refer (require-functions require-resources)]
   [re-cog.common :refer (require-constants)]
   [re-cog.facts.config :refer (configuration)]
   [re-cog.common.defs :refer (def-inline)]))

(require-functions)
(require-resources)
(require-constants)

(def-inline prequisits
  "docker install prequisits"
  []
  (doseq [p ["apt-transport-https" "ca-certificates" "gnupg-agent" "software-properties-common"]]
    (package p :present)))

(def-inline {:depends 're-cog.recipes.docker/prequisits} install
  "install docker"
  []
  (let [listing "/etc/apt/sources.list.d/docker-ce.list"
        url "https://download.docker.com/linux/ubuntu/gpg"
        keyrings "/usr/share/keyrings/"
        key "docker-gpg"
        repo (<< "deb [arch=amd64] https://download.docker.com/linux/ubuntu bionic stable")]
    (set-file-acl "re-ops" "rwX" keyrings)
    (download url (<< "~{keyrings}/~{key}") "1500c1f56fa9e26b9b8f42452a553675796ade0807cdce11975eb98170b3a570")
    (key-file (<< "~{keyrings}/~{key}"))
    (file listing :present)
    (line listing repo :present)
    (update-)
    (doseq [p ["docker-ce" "docker-ce-cli" "containerd.io"]]
      (package p :present))))

(def-inline {:depends 're-cog.recipes.docker/install} passwordless
  "Enable passwordless docker"
  []
  (let [{:keys [user]} (configuration)]
    (group-add "docker" user)))
