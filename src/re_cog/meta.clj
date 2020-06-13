(ns re-cog.meta
  "Re-cog functions metadata used in Re-mote/gent send")

(defn resolve- [n]
  (require n)
  {n (into {}
           (filter
            (fn [[s f]]
              (:serializable.fn/source (meta (deref f)))) (ns-publics n)))})

(defn resources
  "Resource function names must be unique (so we can inline them without looking up the ns)"
  []
  {:post [#(distinct? (flatten (map keys (vals %))))]}
  (apply merge
         (map resolve-
              [; resources
               're-cog.resources.exec
               're-cog.resources.ufw
               're-cog.resources.disk
               're-cog.resources.permissions
               're-cog.resources.service
               're-cog.resources.file
               're-cog.resources.package
               're-cog.resources.git
               're-cog.resources.download
               're-cog.resources.archive
               're-cog.resources.user
               're-cog.resources.dconf
               ; facts
               're-cog.facts.oshi
               're-cog.facts.datalog
               're-cog.facts.osquery
               're-cog.facts.security])))

(defn resource-functions
  "Flatten list of function to var map"
  []
  (apply merge (vals (resources))))
