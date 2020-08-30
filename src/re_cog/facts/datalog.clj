(ns re-cog.facts.datalog
  (:require
   [clojure.string :refer  (join split)]
   [re-cog.common.defs :refer (def-serial)]
   [taoensso.timbre :refer (info)]
   [camel-snake-kebab.core :as csk]
   [re-share.oshi :refer (operating-system hardware)]
   [clojure.java.shell :refer [sh]]
   [datascript.core :as d]))

(def db (atom nil))

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
        keyz (map (comp csk/->kebab-case name) (id-and-key true))
        suffix (join "." (rest keyz))
        datom-k (if-not (empty? suffix) (keyword (first keyz) suffix) (keyword (first keyz)))]
    [[datom-k v] (.hashCode (join "." (or (butlast ks) ks)))]))

(defn fact-pairs [ms]
  (map (fn [[id fs]] [id (into {} (map first fs))]) ms))

(defn add-oshi-section [s]
  (let [joined (group-by second (map join-keys (flatten-keys s)))]
    (doseq [[id m] (fact-pairs joined)]
      (let [purged (into {} (filter second m))]
        (d/transact! db (with-id id purged))))))

(defn jvm-properties []
  (into {}
        (map
         (fn [[k v]] [(keyword (join "/" (split k #"\." 2))) v]) (into {} (System/getProperties)))))

(defn add-properties [properties]
  (d/transact! db (with-id (.hashCode properties) properties)))

(defn populate
  "Add all facts to the DB"
  []
  (reset! db (d/empty-db))
  (d/transact! db (with-id 1 {:os.desktop (desktop?)}))
  (add-oshi-section (operating-system))
  (add-oshi-section (hardware))
  (add-properties (jvm-properties))
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
   '[:find ?f :where
     [_ :os/desktop true]
     [?f :family "Ubuntu"]]))

(defn ubuntu-18.04-desktop?
  "Are we running in Ubuntu desktop"
  []
  (verify
   '[:find ?e ?f ?v :where
     [?e :os/desktop true]
     [?f :family "Ubuntu"]
     [_ :version-info/version ?v]
     [(clojure.string/starts-with? ?v "18.04")]]))

(defn ubuntu-20_04-desktop?
  "Are we running in Ubuntu desktop"
  []
  (verify
   '[:find ?v :where
     [_ :os/desktop true]
     [_ :family "Ubuntu"]
     [_ :version-info/version ?v]
     [(clojure.string/starts-with? ?v "20.04")]]))

(defn ubuntu-version
  "Ubuntu major version number"
  []
  (let [q '[:find ?v :where [_ :family "Ubuntu"] [_ :version-info/version ?v]]]
    (->> q singleton (re-find #"\d+.\d+") (BigDecimal.))))

(defn os
  "Grab the current operating system"
  []
  (keyword (clojure.string/replace (singleton '[:find ?os :where [_ :family ?os]]) #"\s" "_")))

(defn hostname
  "Grab the current hostname"
  []
  (singleton '[:find ?hostname :where [_ :network-params/host-name ?hostname]]))

(defn fqdn
  "Grab the current full qualified domain name"
  []
  (singleton '[:find ?fqdn :where [_ :network-params/domain-name ?fqdn]]))

(comment
  (query '[:find ?v :where [_ :version-info/version ?v]])
  (query '[:find ?v :where [_ :java/version ?v]])
  (query '[:find ?v ?n :where [?e :disk-stores/size ?v] [?e :disk-stores/name ?n]])
  (unknown-disks)
  (populate))
