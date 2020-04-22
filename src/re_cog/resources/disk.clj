(ns re-cog.resources.disk
  "Disk managment resources"
  (:require
   [clojure.core.strint :refer (<<)]
   [re-cog.resources.exec :refer (run)]
   [re-cog.common.functions :refer (require-functions)]
   [re-cog.common.defs :refer (def-serial)]))

(require-functions)

(def-serial partition-
  "Create a single partition of ext4 on a device and mount"
  [device]
  (letfn [(partition-script []
            (script
             ("sudo" "/usr/sbin/parted" ~device "--script" "--" "mklabel" "msdos")
             ("sudo" "/usr/sbin/parted" "-s" "-a" "optimal" "--" ~device "mkpart" "primary" "0" "-1")
             ("sudo" "/usr/sbin/mkfs.ext4" ~device "-F")))]
    (run partition-script)))

(def-serial mount
  "Mount a disk"
  [device target]
  (letfn [(mount-script []
            (script ("sudo" "/usr/bin/mount" "-o" "rw" ~device ~target)))]
    (run mount-script)))

