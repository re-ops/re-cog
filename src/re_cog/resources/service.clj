(ns re-cog.resources.service
  (:require
   [re-cog.common.defs :refer (def-serial)]
   [re-cog.common.functions :refer (require-functions)]
   [re-cog.common.constants :refer (require-constants)]))

(require-functions)
(require-constants)

(def-serial service
  "Service start/stop/restart"
  [srv state]
  (letfn [(service- [srv state]
            (fn []
              (script
               ("sudo" ~systemd-bin ~srv ~state))))]
    (assert (#{:start :stop :restart} state))
    (run- (service- srv (name state)))))

(def-serial on-boot
  "Manage on boot service state enable/disable"
  [srv state]
  (letfn [(set-boot [srv stats]
            (fn []
              (script
               ("sudo" ~systemctl-bin ~state ~srv))))]
    (assert (#{:enable :disable} state))
    (run- (set-boot srv (name state)))))
