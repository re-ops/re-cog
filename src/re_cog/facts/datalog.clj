(ns re-cog.facts.datalog
  (:require
   [re-cog.common.defs :refer (def-serial def-inline)]
   [taoensso.timbre :refer (info)]
   [camel-snake-kebab.core :as csk]
   [re-share.oshi :refer (operating-system hardware)]
   [clojure.java.shell :refer [sh]]
   [datascript.core :as d]))

(def db (d/create-conn))

(defn desktop?
  "check if we are running within a desktop machine"
  []
  (= (:exit (sh "bash" "-c" "type Xorg")) 0))

(defn with-id
  "Create a datom with an id"
  [id m]
  [(assoc m :db/id id)])

(defn add-ns [n m]
  (into {} (map (fn [[k v]] [(keyword (name n) (name k)) v]) m)))

(defn add-items [n ms]
  (doseq [fact (map (partial add-ns n) ms)]
    (d/transact! db (with-id -1 fact))))

(defn add-oshi-seq [n ms]
  (letfn [(filter-nils [m] (filter (fn [[_ v]] (not (nil? v))) m))
          (kebab-keys [m] (map (fn [[k v]] [(csk/->kebab-case k) v]) m))]
    (add-items n (map (fn [m] (into {} (kebab-keys (filter-nils m)))) ms))))

(defn add-oshi-section [s]
  (doseq [[n m] s]
    (if (sequential? m)
      (add-oshi-seq (csk/->kebab-case n) m)
      (if (map? m)
        (d/transact! db (with-id -1 (add-ns (csk/->kebab-case n) m)))
        (d/transact! db (with-id -1 {n m}))))))

(defn populate
  "Add all facts to the DB"
  []
  (d/transact! db (with-id 1 {:os/desktop (desktop?)}))
  (add-oshi-section (operating-system))
  (add-oshi-section (hardware))
  (info "Loaded facts into datascript db"))

; Queries

(defn unknown-disks []
  (d/filter @db
            (fn [db datom]
              (and (= "disk-stores" (namespace (:a datom)))
                   (let [eid (:e datom)
                         e (d/entity db eid)]
                     (not= (:disk-stores/model e) "Unknown"))))))

(def running-processes
  '[:find ?name
    :where
    [?e :services/state "RUNNING"]
    [?e :services/name ?name]])

(defn query
  "Run a remote datalog query against our facts db"
  [q]
  (assert (not (empty? @db)))
  (d/q q @db))

(defn verify
  "Run a query and check if its true (we get a non empty result)"
  [query]
  (assert (not (empty? @db)))
  (not (empty? (d/q query @db))))

(defn singleton
  "Run a remote datalog query against our facts db"
  [q]
  (assert (not (empty? @db)))
  (-> q (d/q @db) first first))

; facts

(defn ubuntu-desktop?
  "Are we running in Ubuntu desktop"
  []
  (verify
   '[:find ?e ?f :where
     [?e :os/desktop true]
     [?f :family "Ubuntu"]]))

(defn ubuntu-18.04-desktop?
  "Are we running in Ubuntu desktop"
  []
  (verify
   '[:find ?e ?f ?v :where
     [?e :os/desktop true]
     [?f :family "Ubuntu"]
     [?v :version/version "18.04"]]))

(defn ubuntu-19.10-desktop?
  "Are we running in Ubuntu desktop"
  []
  (verify
   '[:find ?v :where
     [?e :os/desktop true]
     [?f :family "Ubuntu"]
     [?v :version/version "19.10"]]))

(defn os
  "Grab the current operating system"
  []
  (keyword (singleton '[:find ?os :where [_ :family ?os]])))

(comment
  (query '[:find ?v :where [_ :version/version ?v]])
  (populate))
