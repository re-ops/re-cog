(ns re-cog.scripts.restic
  (:require
   [pallet.stevedore :refer (script)]
   [clojure.string :refer (upper-case)]
   [clojure.core.strint :refer (<<)]))

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
       ("/usr/bin/restic" "backup" ~src "-r" ~target)))))

(defn check
  "restic backup script"
  [{:keys [dest pass id key type] :as b}]
  (fn []
    (let [target (<< "~{type}:~{dest}")]
      (script
       ("export" (set! RESTIC_PASSWORD ~(escape pass)))
       ("export" (set! ~(prefix type "ACCOUNT_KEY") ~(escape key)))
       ("export" (set! ~(prefix type "ACCOUNT_ID") ~(escape id)))
       ("/usr/bin/restic" "check" "-r" ~target)))))

(defn restore
  "restic backup script"
  [{:keys [dest pass id key type] :as b} target]
  (fn []
    (let [source (<< "~{type}:~{dest}")]
      (script
       ("export" (set! RESTIC_PASSWORD ~(escape pass)))
       ("export" (set! ~(prefix type "ACCOUNT_KEY") ~(escape key)))
       ("export" (set! ~(prefix type "ACCOUNT_ID") ~(escape id)))
       ("/usr/bin/restic" "restore" "latest" "-r" ~source "--target" ~target "--verify")))))
