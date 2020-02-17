(ns re-cog.common.datalog
  "Common datascript utilities"
  (:require [datascript.core :as d]))

(defn with-id
  "Create a datom with an id"
  [id m]
  [(assoc m :db/id id)])

(defn add-ns [n m]
  (into {} (map (fn [[k v]] [(keyword (name n) (name k)) v]) m)))

(defn add-items [db n ms]
  (doseq [fact (map (partial add-ns n) ms)]
    (d/transact! db (with-id -1 fact))))

