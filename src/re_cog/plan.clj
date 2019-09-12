(ns re-cog.plan
  "Execution plans support"
  (:require
   [loom.alg :as alg]
   [loom.graph :as g]))

(def base-setup
  ['re-cog.recipes.shell 're-cog.recipes.hardening 're-cog.recipes.osquery 're-cog.recipes.monitoring])

(def virtual (into ['re-cog.recipes.virtualization] base-setup))

(def backup (into ['re-cog.recipes.backup] base-setup))

(def nas (into ['re-cog.recipes.backup 're-cog.recipes.zfs] base-setup))

(def wireguard ['re-cog.recipes.hardening 're-cog.recipes.wireguard])

(def dev (into ['re-cog.recipes.clojure 're-cog.recipes.build 're-cog.recipes.nvim 're-cog.recipes.graal] base-setup))

(defn all-functions [namespaces]
  (mapcat
   (fn [n] (vals (ns-publics n))) namespaces))

(defn dep-nodes [fns]
  (mapcat
   (fn [f]
     (when-let [deps (:depends (meta f))]
       (if-not (seq? deps)
         (list [deps f])
         (map (fn [d] [d f]) deps)))) fns))

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
