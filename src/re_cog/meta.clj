(ns re-cog.meta
  "Re-cog functions metadata used in Re-mote/gent send"
  (:require
   re-cog.recipes.build
   re-cog.recipes.osquery
   re-cog.resources.exec
   re-cog.resources.download
   re-cog.resources.archive
   re-cog.resources.file
   re-cog.resources.package
   re-cog.facts.oshi
   re-cog.facts.query
   re-cog.facts.security))

(def functions
  (apply merge
         (map ns-map
              ['re-cog.resources.exec
               're-cog.resources.file
               're-cog.resources.package
               're-cog.facts.oshi
               're-cog.facts.query
               're-cog.facts.security
               're-cog.resources.download
               're-cog.resources.archive
               're-cog.recipes.osquery
               're-cog.recipes.build])))

(defn fn-meta [f]
  {:post [#(not (nil? %))]}
  (meta
   (second
    (first
     (filter #(and (var? (second %)) (= f (var-get (second %)))) functions)))))

