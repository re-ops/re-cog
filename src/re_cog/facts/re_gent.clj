(ns re-cog.facts.re-gent
  (:require
   [re-cog.common.defs :refer (def-serial)]))

(defn pool-status
  "Get re-gent thread pool status (returns nil if not running in the context of re-gent)"
  []
  (when-let [pool (resolve 're-gent.core/pool)]
    (let [m (-> pool deref deref bean)
          q (m :queue)]
      (-> m
          (dissoc :threadFactory :rejectedExecutionHandler :class :queue)
          (assoc :queue-size (.size q))))))
