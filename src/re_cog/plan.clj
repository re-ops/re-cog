(ns re-cog.plan
  "Execution plans support"
  (:require
   [re-cog.common.datalog :refer (add-ns with-id)]
   [datascript.core :as d]
   [loom.alg :as alg]
   [loom.graph :as g]))

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

; recipe metadata
;; (defn all-recipes []
;;   (filter
;;    (fn [n] (.contains (str n) "re-cog.recipe"))
;;    (map ns-name (all-ns))))
;;
;; (def db (d/create-conn))
;;
;; (defn populate []
;;   (doseq [m (map meta (all-functions (all-recipes))) :let [n (ns-name (:ns m))]]
;;     (d/transact! db (with-id -1 (add-ns n m)))))
;;


(comment
  (clojure.pprint/pprint @db)
  (doseq [n base-desktop] (require n))
  (populate)
  (loom.io/view (execution-plan iot-dev)))
