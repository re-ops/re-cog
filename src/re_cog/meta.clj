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
               're-cog.resources.permissions
               're-cog.resources.service
               're-cog.resources.file
               're-cog.resources.package
               're-cog.resources.git
               're-cog.resources.download
               're-cog.resources.archive
               're-cog.resources.user
               ; facts
               're-cog.facts.oshi
               're-cog.facts.query
               're-cog.facts.security])))

(defn recipes
  "Recipe function names (not inlined so not uniquly named)"
  []
  (apply merge
         (map resolve-
              ['re-cog.recipes.nvim
               're-cog.recipes.build
               're-cog.recipes.gcloud
               're-cog.recipes.osquery
               're-cog.recipes.shell
               're-cog.recipes.virtualization
               're-cog.recipes.backup
               're-cog.recipes.clojure
               're-cog.recipes.hardening
               're-cog.recipes.monitoring
               're-cog.recipes.zfs
               're-cog.recipes.wireguard
               're-cog.recipes.graal])))

(defn resource-functions
  "Flatten list of function to var map"
  []
  (apply merge (vals (resources))))

(defn meta-from
  "Function metadata (for remote execution)"
  [f m]
  {:post [#(not (nil? %))]}
  (meta
   (second
    (first
     (filter #(and (var? (second %)) (= f (var-get (second %)))) m)))))

(defn fn-meta [f]
  (first (filter identity (map (fn [[_ v]] (meta-from f v)) (merge (recipes) (resources))))))
