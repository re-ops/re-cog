(ns re-cog.resources.user
  "User/Group manipulation support"
  (:require
   [clojure.java.shell :refer (sh)]
   [re-cog.common.defs :refer (def-serial)]))

(def-serial group-add
  "Add user to group:

    (group-add \"libvirt\" \"re-ops\")
  "
  [group user]
  (sh "sudo" "/usr/sbin/usermod" "-G" group "-a" user))
