(ns re-cog.common.datalog
  "Common datascript utilities"
  (:require
   [camel-snake-kebab.core :as csk]
   [datascript.core :as d]))

(defn with-id
  "Create a datom with an id"
  [id m]
  [(assoc m :db/id id)])

(defn add-ns
  "Add namespace n to all the keys in m"
  [n m]
  (into {} (map (fn [[k v]] [(keyword (name n) (name k)) v]) m)))

(defn add-items
  "Add a sequence of items (ms) with namespace n"
  [db n ms]
  (doseq [fact (map (partial add-ns n) ms)]
    (d/transact! db (with-id -1 fact))))

(defn flatten-keys* [a ks m]
  (cond
    (map? m) (reduce into (map (fn [[k v]] (flatten-keys* a (conj ks k) v)) (seq m)))
    (sequential? m) (reduce into (map-indexed (fn [k v] (flatten-keys* a (conj ks k) v)) m))
    :else (assoc a ks m)))

(defn flatten-keys [m]
  (flatten-keys* {} [] m))

(defn join-keys [[ks v]]
  (let [id-and-key (group-by keyword? ks)]
    [(clojure.string/join "/" (map csk/->kebab-case (id-and-key true))) (clojure.string/join "" (id-and-key false)) v]))

(comment
  (flatten-keys {:one '({:two {:three 3}} {:1 2})}))
