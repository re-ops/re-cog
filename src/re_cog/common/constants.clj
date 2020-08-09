(ns re-cog.common.constants)

(def apt-bin "/usr/bin/apt")

(def ufw-bin "/usr/sbin/ufw")

(def dpkg-bin "/usr/bin/dpkg")

(def systemd-bin "/usr/sbin/service")

(def systemctl-bin "/bin/systemctl")

(def sysctl-bin "/usr/sbin/sysctl")

(def openssl-bin "/usr/bin/openssl")

(def cat-bin "/usr/bin/cat")

(defn require-constants
  "Require common constant values"
  []
  (require
   '[re-cog.common.constants :refer [apt-bin dpkg-bin systemd-bin systemctl-bin ufw-bin sysctl-bin openssl-bin cat-bin]]))
