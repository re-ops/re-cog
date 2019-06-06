(ns re-cog.facts.query
  "query functions"
  (:require
   [clojure.java.shell :refer [sh]]
   [cheshire.core :refer (parse-string)]
   [re-cog.resources.common :refer (defn)]))

(defn osquery
  "Run a osquery query"
  [query]
  (parse-string (:out (sh "/usr/bin/osqueryi" "--json" query)) true))

