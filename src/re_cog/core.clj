(ns re-cog.core
  (:require
   re-cog.resources.exec
   re-cog.resources.file
   re-cog.resources.package
   re-cog.facts.oshi
   re-cog.facts.query
   re-cog.facts.security))

(def functions
  (apply merge
         (map ns-map
              ['re-cog.resources.exec 're-cog.resources.file 're-cog.resources.package
               're-cog.facts.oshi 're-cog.facts.query 're-cog.facts.security])))

(defn fn-meta [f]
  {:post [#(not (nil? %))]}
  (meta
   (second
    (first
     (filter #(and (var? (second %)) (= f (var-get (second %)))) functions)))))
