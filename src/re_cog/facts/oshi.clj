(ns re-cog.facts.oshi
  (:require
   [serializable.fn :as s]
   [re-cog.common :refer (def-serial)]
   [re-share.oshi :refer (read-metrics os get-processes)]))

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

(def-serial operating-system
  "Getting all OS information using oshi"
  []
  (get-in (read-metrics) [:operatingSystem]))

(def-serial hardware
  "Getting all Hardware information using oshi"
  []
  (get-in (read-metrics) [:hardware]))

