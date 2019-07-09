(ns re-cog.plan
  "Execution plans support"
  (:require
   [loom.alg :as alg]
   [loom.graph :as g]))

(def base-machine
  ['re-cog.recipes.osquery 're-cog.recipes.build])

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
  (loom.io/view (execution-plan base-machine))
  (dep-nodes (all-functions base-machine))
  (def base-machine (g/graph [1 2]))
  (g/view base-machine))
