(ns re-cog.facts.security
  "security related functions"
  (:require
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
