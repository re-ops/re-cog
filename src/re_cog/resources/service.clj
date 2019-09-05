(ns re-cog.resources.service
  (:require
   [re-cog.resources.exec :refer (run)]
   [re-cog.common.functions :refer (require-functions)]
   [re-cog.common.defs :refer (def-serial)]
   [re-cog.common :refer (require-constants)]))

(require-functions)
(require-constants)

#_(defn sysctl [action service]
    (go
      (let [release (<! (os :release))]
        (if-let [bin (<! (sysctl-bin release))]
          (<! (sh bin action (<< "~{service}.service")))
          {:error (<< "sysctl binary not found for os release ~{release}")}))))

#_(defrecord Systemd []
    Service
    (disable [this service]
      (debug "disabling service" ::systemd)
      (go
        (<! (sysctl "disable" service))))

    (enable [this service]
      (debug "enabling service" ::systemd)
      (go
        (<! (sysctl "enable" service)))))

(def-serial service
  "Service start/stop/restart"
  [srv state]
  (do
    (assert (#{:start :stop :restart} state))
    (sh! "sudo" "/usr/sbin/service" srv (name state))))

(def-serial on-boot
  "Manage on boot service state enable/disable"
  [srv state]
  (do (assert (#{:enable :disable} state))
      (sh! "sudo" systemctl-bin (name state) (<< "~{srv}.service"))))
