(ns re-cog.meta
  "Re-cog functions metadata used in Re-mote/gent send")

(defn resolve- [n]
  (require n)
  (into {}
        (filter
         (fn [[s f]] (:serializable.fn/source (meta (deref f)))) (ns-publics n))))

(defn functions []
  (apply merge
         (map resolve-
              ['re-cog.resources.exec
               're-cog.resources.file
               're-cog.resources.package
               're-cog.resources.git
               're-cog.resources.download
               're-cog.resources.archive
               're-cog.facts.oshi
               're-cog.facts.query
               're-cog.facts.security])))

(defn fn-meta [f]
  {:post [#(not (nil? %))]}
  (meta
   (second
    (first
     (filter #(and (var? (second %)) (= f (var-get (second %)))) (functions))))))

