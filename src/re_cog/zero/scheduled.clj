(ns re-cog.zero.scheduled
  "Scheduled results handling"
  (:require
   [clojure.core.strint :refer (<<)]
   [re-cog.common.defs :refer (def-serial)]))

(def scheduled-results (atom {}))

(defn get-result [k]
  (get (deref scheduled-results) k
       {:exit 1 :out (<< "missing result for ~{k} in scheduled results") :err ""}))

(def-serial get-scheduled-result
  "Get a result for a scheduled function"
  [k]
  (get-result k))
