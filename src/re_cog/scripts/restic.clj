(ns re-cog.scripts.restic
  (:require
   [pallet.stevedore :refer (script)]
   [clojure.string :refer (upper-case)]
   [clojure.core.strint :refer (<<)]))

(def restic-bin "/usr/local/bin/restic")

(defn escape [s]
  (str \" s \"))

(defn prefix [t suffix]
  (<< "~(upper-case (name t))_~{suffix}"))

(defn backup
  "restic backup script"
  [{:keys [src dest pass id key type] :as b}]
  (fn []
    (let [target (<< "~{type}:~{dest}")]
      (script
       ("export" (set! RESTIC_PASSWORD ~(escape pass)))
       ("export" (set! ~(prefix type "ACCOUNT_KEY") ~(escape key)))
       ("export" (set! ~(prefix type "ACCOUNT_ID") ~(escape id)))
       (~restic-bin "backup" ~src "-r" ~target)))))

(defn run
  "A single arg action script"
  [action {:keys [dest pass id key type] :as b}]
  (fn []
    (let [target (<< "~{type}:~{dest}")]
      (script
       ("export" (set! RESTIC_PASSWORD ~(escape pass)))
       ("export" (set! ~(prefix type "ACCOUNT_KEY") ~(escape key)))
       ("export" (set! ~(prefix type "ACCOUNT_ID") ~(escape id)))
       (~restic-bin ~action "-r" ~target)))))

(defn check
  "Check a backup"
  [m]
  (run "check" m))

(defn unlock
  "Unlock a backup lock"
  [m]
  (run "unlock" m))

(defn init
  "Initialize a backup"
  [m]
  (run "init" m))

(defn restore
  "restic backup script"
  [{:keys [dest pass id key type] :as b} target]
  (fn []
    (let [source (<< "~{type}:~{dest}")]
      (script
       ("export" (set! RESTIC_PASSWORD ~(escape pass)))
       ("export" (set! ~(prefix type "ACCOUNT_KEY") ~(escape key)))
       ("export" (set! ~(prefix type "ACCOUNT_ID") ~(escape id)))
       (~restic-bin "restore" "latest" "-r" ~source "--target" ~target "--verify")))))
