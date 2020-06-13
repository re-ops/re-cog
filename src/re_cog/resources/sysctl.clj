(ns re-cog.resources.sysctl
  "Adding sysctl values and loading them"
  (:require
   [re-cog.common.functions :refer (require-functions)]
   [re-cog.common.constants :refer (require-constants)]
   [re-cog.common.defs :refer (def-serial)]))

(require-functions)
(require-constants)

(def-serial reload
  "Reload sysctl file
    (reload \"/etc/sysctl.d/10-foo.conf\")
   "
  [target]
  (letfn [(reload-file [target]
            (fn []
              (script ("sudo" ~sysctl-bin "-e" "-p" ~target))))]
    (run- (reload-file target))))
