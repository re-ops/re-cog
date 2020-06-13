(ns re-cog.resources.service
  (:require
   [re-cog.common.functions :refer (require-functions)]
   [re-cog.common.defs :refer (def-serial)]
   [re-cog.common.constants :refer (require-constants)]))

(require-functions)
(require-constants)

(def-serial service
  "Service start/stop/restart"
  [srv state]
  (do
    (assert (#{:start :stop :restart} state))
    (sh "sudo" systemd-bin srv (name state))))

(def-serial on-boot
  "Manage on boot service state enable/disable"
  [srv state]
  (do (assert (#{:enable :disable} state))
      (let [{:keys [out] :as m} (sh systemctl-bin "list-units" "--full" "-all")]
        (if (.contains out srv)
          (sh "sudo" systemctl-bin (name state) (<< "~{srv}.service"))
          m))))
