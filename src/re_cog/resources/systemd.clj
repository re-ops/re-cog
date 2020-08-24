(ns re-cog.resources.systemd
  "Systemd related resources"
  (:require
   [re-cog.common.defs :refer (def-serial)]
   [re-cog.common.functions :refer (require-functions)]
   [re-cog.common.constants :refer (require-constants)]))

(require-functions)
(require-constants)

(def-serial user-service
  "Enable a local user service"
  [home description exec name]
  (let [dest (<< "~{home}/.config/systemd/user/")
        source (slurp (io/resource "main/resources/user-service.mustache"))
        out (render source {:working-directory home :exec exec :description description})
        service (<< "~{name}.service")]
    (letfn [(enable []
              (script (~systemctl-bin "--user" "enable" ~service)))]
      (fs/mkdirs dest)
      (spit (<< "~{dest}/~{service}") out)
      (run- enable)
      (success (<< "user service created")))))
