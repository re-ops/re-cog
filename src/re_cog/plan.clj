(ns re-cog.plan
  "Execution plans support"
  (:require
   [loom.alg :as alg]
   [loom.graph :as g]))

(def base-setup
  ['re-cog.recipes.shell])

(def osquery (into ['re-cog.recipes.osquery] base-setup))

(def virtual (into ['re-cog.recipes.virtualization] base-setup))

(def backup (into ['re-cog.recipes.backup] base-setup))

(def dev (into ['re-cog.recipes.clojure 're-cog.recipes.build 're-cog.recipes.nvim] base-setup))

(defn all-functions [namespaces]
  (mapcat
   (fn [n] (vals (ns-publics n))) namespaces))

(defn dep-nodes [fns]
  (mapcat
   (fn [f]
     (when-let [deps (:depends (meta (deref f)))]
       (if-not (seq? deps)
         (list [f deps])
         (map (fn [d] [f d]) deps)))) fns))

(defn execution-graph [namespaces]
  (let [fs (all-functions namespaces)
        deps (dep-nodes fs)]
    (apply g/digraph (concat deps fs))))

(defn execution-plan [namespaces]
  (alg/topsort (execution-graph namespaces)))

(comment
  (require
   're-cog.recipes.osquery
   're-cog.recipes.build)
  (loom.io/view (execution-plan base-machine)))
