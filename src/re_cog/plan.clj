(ns re-cog.plan
  "Execution plans support"
  (:require
   [loom.alg :as alg]
   [loom.graph :as g]))

(def ^{:doc "Base setup common to all plans (shell, hardening, osquery etc.)"}
  base ['re-cog.recipes.shell 're-cog.recipes.hardening 're-cog.recipes.osquery 're-cog.recipes.monitoring])

(def ^{:doc "Virtualization tools (KVM, LXC)"}
  virtual (into ['re-cog.recipes.virtualization] base))

(def ^{:doc "Docker server"}
  docker (into ['re-cog.recipes.docker] base))

(def ^{:doc "Backup tools"}
  backup (into ['re-cog.recipes.backup] base))

(def ^{:doc "Cloud tools (gcloud, doctl, awscli)"}
  cloud (into ['re-cog.recipes.cloud] base))

(def nas (into ['re-cog.recipes.backup 're-cog.recipes.zfs] base))

(def wireguard ['re-cog.recipes.hardening 're-cog.recipes.wireguard])

(def ^{:doc "An instance with just nvim"}
  editing ['re-cog.recipes.nvim])

(def ^{:doc "Development machine with Clojure and Graal"}
  dev (into ['re-cog.recipes.clojure 're-cog.recipes.build 're-cog.recipes.nvim 're-cog.recipes.graal] base))

(def ^{:doc "Development machine with Clojure and deep learning utils"}
  learning (into ['re-cog.recipes.clojure 're-cog.recipes.build 're-cog.recipes.nvim 're-cog.recipes.deep] base))

(def ^{:doc "re-core ready instances"}
  core ['re-cog.recipes.clojure 're-cog.recipes.build 're-cog.recipes.nvim 're-cog.recipes.shell 're-cog.recipes.hardening])

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
