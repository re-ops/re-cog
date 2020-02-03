(ns re-cog.facts.oshi
  (:require
   [serializable.fn :as s]
   [re-cog.common.defs :refer (def-serial)]
   [re-share.oshi :refer (read-all get-processes)]))

(def-serial all-processes
  "Get all processes"
  []
  (get-processes))

(def ^{:doc "Filter process by name"} named
  (fn [target]
    (s/fn [proc] (= (proc :name) target))))

(def-serial processes-by
  "Get processes by fn"
  [f]
  (let [f' (eval f)]
    (filter f' (get-processes))))

