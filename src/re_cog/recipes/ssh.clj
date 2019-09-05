(ns re-cog.recipes.ssh
  "ssh setup and hardedning"
  (:require
   [re-cog.facts.config :refer (configuration)]
   [re-cog.resources.file :refer (line line-set)]
   [re-cog.resources.service :refer (service)]
   [re-cog.common.functions :refer (require-functions require-resources)]
   [re-cog.common :refer (require-constants)]
   [re-cog.common.defs :refer (def-inline)]))

(require-functions)
(require-resources)
(require-constants)

(def-inline ssh-server
  "SSH server"
  []
  (package "openssh-server" :present)
  (line-set  "/etc/ssh/sshd_config" "PermitRootLogin" "no" " ")
  (line-set "/etc/ssh/sshd_config" "PasswordAuthentication" "no" " ")
  (line-set "/etc/ssh/sshd_config" "X11Forwarding" "no" " ")
  (line "/etc/ssh/sshd_config" "\nUseDns no" :present)
  (service "ssh" :start))
