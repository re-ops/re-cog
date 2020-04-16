(ns re-cog.scripts.disks
  "Scripts for disk manipulation"
  (:require  [pallet.stevedore :refer (script)]))

(defn partition-
  "Create a single partition of ext4 on a device and mount"
  [device]
  (fn []
    (script
     ("sudo" "/usr/sbin/parted" ~device "--script" "--" "mklabel" "msdos")
     ("sudo" "/usr/sbin/parted" "-s" "-a" "optimal" "--" ~device "mkpart" "primary" "0" "-1")
     ("sudo" "/usr/sbin/mkfs.ext4" ~device "-F"))))

(defn mount [device target]
  (fn []
    (script
     ("sudo" "/usr/bin/mount" ~device ~target))))
