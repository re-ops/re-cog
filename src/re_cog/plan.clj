(ns re-cog.plan
  "Execution plans support"
  (:require
   [re-cog.common.datalog :refer (add-ns with-id)]
   [datascript.core :as d]
   [loom.alg :as alg]
   [loom.graph :as g]))

; Basic profiles
(def ^{:doc "Minimal set of recipes"}
  lean #{'re-cog.recipes.access 're-cog.recipes.shell 're-cog.recipes.tmux 're-cog.recipes.hardening 're-cog.recipes.cleanup})

(def ^{:doc "Base setup common to all plans (shell, hardening, osquery etc.)"}
  base (into #{'re-cog.recipes.osquery 're-cog.recipes.monitoring} lean))

; Infra profiles
(def ^{:doc "re-core ready instances"}
  core #{'re-cog.recipes.clojure 're-cog.recipes.build 're-cog.recipes.nvim 're-cog.recipes.shell 're-cog.recipes.hardening})

(def nas (into #{'re-cog.recipes.backup 're-cog.recipes.zfs} base))

(def wireguard #{'re-cog.recipes.hardening 're-cog.recipes.wireguard})

; Container/Virtualization

(def ^{:doc "Virtualization tools (KVM, LXC)"}
  virtual (into #{'re-cog.recipes.virtualization} base))

(def ^{:doc "minikube"}
  minikube #{'re-cog.recipes.k8s 're-cog.recipes.docker})

(def ^{:doc "Docker server"}
  docker (into #{'re-cog.recipes.docker} base))

(def ^{:doc "Backup tools"}
  backup (into #{'re-cog.recipes.backup} base))

(def ^{:doc "Cloud tools (gcloud, doctl, awscli)"}
  cloud (into #{'re-cog.recipes.cloud} base))

(def ^{:doc "An instance with just nvim"}
  editing #{'re-cog.recipes.nvim})

(def ^{:doc "Security utilities"}
  security #{'re-cog.recipes.security})

; Development profiles

(def ^{:doc "Base dev support"}
  base-dev (into #{'re-cog.recipes.build 're-cog.recipes.nvim} lean))

(def ^{:doc "Clojure development instance"}
  clj-dev (into #{'re-cog.recipes.clojure} base-dev))

(def ^{:doc "Development machine with Clojure and Graal"}
  native-clj (into #{'re-cog.recipes.graal} clj-dev))

(def ^{:doc "Python development machine"}
  python-dev (into #{'re-cog.recipes.nvim 're-cog.recipes.python} lean))

(def ^{:doc "Support for Java/Kotlin and Clojure development"}
  jvm-dev (into #{'re-cog.recipes.intellij} clj-dev))

(def ^{:doc "Development machine with Clojure and deep learning utils"}
  learning (into #{'re-cog.recipes.clojure 're-cog.recipes.build 're-cog.recipes.nvim 're-cog.recipes.deep} lean))

(def ^{:doc "A Vuepress documentation instance"}
  vuepress (into #{'re-cog.recipes.node 're-cog.recipes.nvim} lean))

(def ^{:doc "IoT development instance"}
  iot-dev (into #{'re-cog.recipes.platformio 're-cog.recipes.3dprint} base-dev))

; Desktop profiles

(def base-desktop #{'re-cog.recipes.xmonad 're-cog.recipes.chrome})

(def ^{:doc "JVM dev desktop"}
  jvm-desktop (into base-desktop jvm-dev))

(def ^{:doc "IOT dev desktop"}
  iot-desktop (into base-desktop iot-dev))

(def ^{:doc "Clojure dev desktop"}
  clj-desktop (into base-desktop clj-dev))

(defn all-functions
  "Get all the functions from provided namespaces"
  [namespaces]
  (mapcat
   (fn [n] (vals (ns-publics n))) namespaces))

(defn dep-nodes
  "Get dependencies from fns"
  [fns]
  (mapcat
   (fn [f]
     (when-let [deps (:depends (meta f))]
       (if-not (seqable? deps)
         (list [deps f])
         (map (fn [d] [d f]) deps)))) fns))

(defn execution-graph
  "Create execution graphs from namespaces"
  [namespaces]
  (let [fs (all-functions namespaces)
        deps (dep-nodes fs)]
    (apply g/digraph (concat deps fs))))

(defn execution-plan [namespaces]
  (alg/topsort (execution-graph namespaces)))

(defn all-recipes []
  (filter
   (fn [n] (.contains (str n) "re-cog.recipe"))
   (map ns-name (all-ns))))

; recipe metadata
(def db (d/create-conn))

(defn populate []
  (doseq [m (map meta (all-functions (all-recipes))) :let [n (ns-name (:ns m))]]
    (d/transact! db (with-id -1 (add-ns n m)))))

(comment
  (clojure.pprint/pprint @db)
  (doseq [n base-desktop] (require n))
  (populate)
  (loom.io/view (execution-plan iot-dev)))
