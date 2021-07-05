(ns re-cog.zero.scheduled
  "Scheduled results handling"
  (:require
   [re-cog.common.defs :refer (def-serial)]))

(def scheduled-results (atom {}))

(defn get-result [k]
  ((deref scheduled-results) k))

(def-serial get-scheduled-result
  "Get a result for a scheduled function"
  [k]
  (get-result k))
