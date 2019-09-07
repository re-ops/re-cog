(ns re-cog.recipes.hardening
  "Hardedning and security tools"
  (:require
   [re-cog.facts.config :refer (configuration)]
   [re-cog.facts.query :refer (desktop?)]
   [re-cog.resources.file :refer (line line-set copy)]
   [re-cog.resources.permissions :refer (set-file-acl)]
   [re-cog.resources.service :refer (service on-boot)]
   [re-cog.common.functions :refer (require-functions require-resources)]
   [re-cog.common :refer (require-constants)]
   [re-cog.common.defs :refer (def-inline)]))

(require-functions)
(require-resources)
(require-constants)

(def-inline ssh-haredning
  "SSH server hardening"
  []
  (package "openssh-server" :present)
  (package "rng-tools" :present)
  (set-file-acl "re-ops" "rwX" "/etc/ssh")
  (line-set  "/etc/ssh/sshd_config" "PermitRootLogin" "no" " ")
  (line-set "/etc/ssh/sshd_config" "PasswordAuthentication" "no" " ")
  (line-set "/etc/ssh/sshd_config" "X11Forwarding" "no" " ")
  (line "/etc/ssh/sshd_config" "\nUseDns no" :present)
  (service "ssh" :restart))

(def-inline networking
  "Hardening network"
  []
  (letfn [(sysctl-reload [target]
            (fn []
              (script ("/sbin/sysctl" "-e" "-p" ~target))))]
    (let [target "/etc/sysctl.d/10-network-hardening.conf"]
      (set-file-acl "re-ops" "rwX" "/etc/sysctl.d")
      (copy "/tmp/resources/networking/harden.conf" target)
      (run (sysctl-reload target)))))

(def-inline disable-bluetooth
  "Disabling bluetooth on desktop machines"
  []
  (if (desktop?)
    (on-boot "bluetooth" :disable)
    {}))
