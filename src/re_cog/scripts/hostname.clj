(ns re-cog.scripts.hostname
  (:require  [pallet.stevedore :refer (script)]))

(defn kernel-hostname
  "Set hosname in kernel"
  [hostname fqdn]
  (script
   (set! HOSTNAME ~hostname)
   (set! FQDN (quoted ~fqdn))
   (pipe ("echo" "kernel.hostname=${HOSTNAME}") ("sudo" "tee" "-a"  "/etc/sysctl.conf"))
   (pipe ("echo" "kernel.domainname=${FQDN}") ("sudo" "tee" "-a"  "/etc/sysctl.conf"))
   ("sudo" "/usr/sbin/sysctl" "--system")))

(defn override-hostname
  "sets hostname and hosts file"
  [hostname fqdn]
  (script
   (pipe ("echo" ~hostname) ("sudo" "tee" "/etc/hostname"))
   (pipe ("echo" "127.0.1.1" ~fqdn ~hostname) ("sudo" "tee" "-a" "/etc/hosts"))))

(defn redhat-hostname
  "Sets up hostname under /etc/sysconfig/network in redhat based systems"
  [fqdn]
  (let [r1 "'s/^HOSTNAME=.*/HOSTNAME=~{fqdn}'"]
    (script
     ("grep -q '^HOSTNAME=' /etc/sysconfig/network ")
     (if (= "$?" 0)
       ("sudo" "sed" "-i" ~r1 "/etc/sysconfig/network")
       ("sudo sed -i '$ a\\HOSTNAME=~{fqdn}' /etc/sysconfig/network")))))
