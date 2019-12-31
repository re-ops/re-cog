(ns re-cog.facts.security
  "security related functions"
  (:require
   [me.raynes.fs :as fs]
   [clojure.java.io :as io]
   [re-scan.core :refer [into-ports into-hosts nmap]]
   [re-cog.common.defs :refer (def-serial)]))

(def-serial scan-ports
  "Open ports nmap scan"
  [path flags network]
  (apply merge (into-ports (nmap path flags network))))

(def-serial scan-hosts
  "Host addresses nmap scan"
  [path flags network]
  (into-hosts (nmap path flags network)))

(def-serial cpu-vulns
  "Grab a list of cpu vulns for a host"
  []
  (into {}
        (map (fn [f] [(keyword (.getName f)) (slurp f)])
             (filter fs/file? (file-seq (io/file "/sys/devices/system/cpu/vulnerabilities/"))))))
