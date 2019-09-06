(ns re-cog.facts.query
  "query functions"
  (:require
   [clojure.java.shell :refer [sh]]
   [cheshire.core :refer (parse-string)]
   [re-cog.common.defs :refer (def-serial)]))

(def-serial osquery
  "Run a osquery query"
  [query]
  (parse-string (:out (sh "/usr/bin/osqueryi" "--json" query)) true))

(defn desktop?
  "check if we are running within a desktop machine"
  []
  (= (:exit (sh "bash" "-c" "type Xorg")) 0))
