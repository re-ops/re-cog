(ns re-cog.common.constants)

(def apt-bin "/usr/bin/apt")

(def ufw-bin "/usr/sbin/ufw")

(def dpkg-bin "/usr/bin/dpkg")

(def systemd-bin "/usr/sbin/service")

(def systemctl-bin "/bin/systemctl")

(defn require-constants
  "Require common constant values"
  []
  (require
   '[re-cog.common.constants :refer [apt-bin dpkg-bin systemd-bin systemctl-bin ufw-bin]]))
