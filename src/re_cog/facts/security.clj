(ns re-cog.facts.security
  "security related functions"
  (:require
   [re-scan.core :refer [into-ports into-hosts nmap]]
   [re-cog.common :refer (defn)]))

(defn scan-ports
  "Open ports nmap scan"
  [path flags network]
  (apply merge (into-ports (nmap path flags network))))

(defn scan-hosts
  "Host addresses nmap scan"
  [path flags network]
  (into-hosts (nmap path flags network)))
