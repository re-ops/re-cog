(ns re-cog.facts.datalog
  (:require
   [clojure.string :refer  (join)]
   [re-cog.common.defs :refer (def-serial)]
   [taoensso.timbre :refer (info)]
   [camel-snake-kebab.core :as csk]
   [re-share.oshi :refer (operating-system hardware)]
   [clojure.java.shell :refer [sh]]
   [datascript.core :as d]))

(def db (d/create-conn))

(defn with-id
  "Create a datom with an id"
  [id m]
  [(assoc m :db/id id)])

(defn desktop?
  "check if we are running within a desktop machine"
  []
  (= (:exit (sh "bash" "-c" "type Xorg")) 0))

(defn flatten-keys* [a ks m]
  (cond
    (map? m) (reduce into (map (fn [[k v]] (flatten-keys* a (conj ks k) v)) (seq m)))
    (sequential? m) (reduce into (map-indexed (fn [k v] (flatten-keys* a (conj ks k) v)) m))
    :else (assoc a ks m)))

(defn flatten-keys [m]
  (flatten-keys* {} [] m))

(defn join-keys [[ks v]]
  (let [id-and-key (group-by keyword? ks)
        datom-k (keyword (join "/" (map (comp csk/->kebab-case name) (id-and-key true))))
        position-k (join "" (id-and-key false))]
    [[datom-k v] (.hashCode (join "/" (or (butlast ks) ks)))]))

(defn fact-pairs [ms]
  (map (fn [[id fs]] [id (into {} (map first fs))]) ms))

(defn add-oshi-section [s]
  (doseq [[id m] (fact-pairs (group-by second (map join-keys (flatten-keys s))))]
    (d/transact! db (with-id id m))))

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

(defn get-db []
  (assert (not (empty? @re-cog.facts.datalog/db)))
  @re-cog.facts.datalog/db)

(def-serial run-query
  "Run a remote datalog query against our facts db"
  [q]
  (d/q q (get-db)))

(defn query
  "Run a remote datalog query against our facts db"
  [q]
  (d/q q (get-db)))

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

(defn ubuntu-20.04-desktop?
  "Are we running in Ubuntu desktop"
  []
  (verify
   '[:find ?v :where
     [?e :os/desktop true]
     [?f :family "Ubuntu"]
     [?v :version/version "20.04"]]))

(defn ubuntu-version
  "Are we running in Ubuntu desktop"
  []
  (let [q '[:find ?v :where [_ :family "Ubuntu"] [_ :version/version ?v]]]
    (-> q singleton (clojure.string/replace #"\\s*LTS" "") (BigDecimal.))))

(defn os
  "Grab the current operating system"
  []
  (keyword (singleton '[:find ?os :where [_ :family ?os]])))

(comment
  (query '[:find ?v :where [_ :version/version ?v]])
  (query '[:find ?v ?n :where [?e :disk-stores/size ?v] [?e :disk-stores/name ?n]])
  (query '[:find ?e ?m  :where [?e :memory/virtualMemory ?m]])
  (unknown-disks)
  (populate))
