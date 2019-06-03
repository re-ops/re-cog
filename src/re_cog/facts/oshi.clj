(ns re-cog.facts.oshi
  (:require
   [serializable.fn :as s]
   [re-share.oshi :refer (read-metrics os get-processes)]))

(def ^{:doc "Get all processes"} all-processes
  (s/fn []
    (get-processes)))

(def ^{:doc "Filter process by name"} named
  (fn [target]
    (s/fn [proc] (= (proc :name) target))))

(def ^{:doc "Get processes by fn"} processes-by
  (s/fn [f]
    (let [f' (eval f)]
      (filter f' (get-processes)))))

(def ^{:doc "Getting all OS information using oshi"} operating-system
  (s/fn []
    (get-in (read-metrics) [:operatingSystem])))

(def ^{:doc "Getting all Hardware information using oshi"} hardware
  (s/fn []
    (get-in (read-metrics) [:hardware])))

