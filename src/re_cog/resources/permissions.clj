(ns re-cog.resources.permissions
  (:require
   [re-cog.common.functions :refer (require-functions)]
   [re-cog.common.defs :refer (def-serial)]))

(require-functions)

(def-serial set-file-acl
  "Set file ACL permissions"
  [user permissions dest]
  (sh! "sudo" "/usr/bin/setfacl" "-R" "-m" (<< "u:~{user}:~{permissions}") dest))
