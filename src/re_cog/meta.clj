(ns re-cog.meta
  "Re-cog functions metadata used in Re-mote/gent send")

(defn resolve- [n]
  (require n)
  (into {}
        (filter
         (fn [[s f]]
           (:serializable.fn/source (meta (deref f)))) (ns-publics n))))

(defn functions []
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
                ; recipes
               're-cog.recipes.nvim
               're-cog.recipes.build
               're-cog.recipes.osquery
               're-cog.recipes.shell
               're-cog.recipes.virtualization
               're-cog.recipes.backup
               're-cog.recipes.clojure
               're-cog.recipes.ssh
               ; facts
               're-cog.facts.oshi
               're-cog.facts.query
               're-cog.facts.security])))

(defn fn-meta [f]
  {:post [#(not (nil? %))]}
  (meta
   (second
    (first
     (filter #(and (var? (second %)) (= f (var-get (second %)))) (functions))))))

