(ns re-cog.facts.oshi
  (:require
   [serializable.fn :as s]
   [re-cog.resources.common :refer (defn)]
   [re-share.oshi :refer (read-metrics os get-processes)]))

(defn all-processes
  "Get all processes"
  []
  (get-processes))

(def ^{:doc "Filter process by name"} named
  (fn [target]
    (s/fn [proc] (= (proc :name) target))))

(defn processes-by
  "Get processes by fn"
  [f]
  (let [f' (eval f)]
    (filter f' (get-processes))))

(defn operating-system
  "Getting all OS information using oshi"
  []
  (get-in (read-metrics) [:operatingSystem]))

(defn hardware
  "Getting all Hardware information using oshi"
  []
  (get-in (read-metrics) [:hardware]))

