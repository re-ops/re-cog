(ns re-cog.resources.dconf
  "Linux Dconf resources"
  (:require
   [me.raynes.fs :as fs]
   [clojure.java.shell :refer (sh)]
   [re-cog.resources.exec :refer (run)]
   [re-cog.common.functions :refer (require-functions)]
   [re-cog.common.defs :refer (def-serial)]))

(require-functions)

(def-serial write
  "Write a single value to dconf key"
  [k v]
  (sh "/usr/bin/dconf" "write" k v))

(def-serial read
  "Read a single value to dconf key"
  [k]
  (update (sh "/usr/bin/dconf" "read" k) :out clojure.string/trim))

(def-serial load-
  "Populate a dconf subpath from m"
  [k m]
  (letfn [(pair [s [k v]]
            (if-not (#{"false" "true"} v)
              (str s (name k) "='" v "'\n")
              (str s (name k) "=" v "\n")))
          (spit-conf [m]
                     (apply str (map (fn [[k vs]] (str "[" k "]\n" (reduce pair "" vs) "\n")) m)))]
    (sh "/usr/bin/dconf" "load" k :in (spit-conf m))))

(defn read-values [kv]
  (let [[k v] (clojure.string/split kv #"=")]
    [(keyword k) (clojure.string/replace v #"'" "")]))

(defn slurp-conf [s]
  (let [dirs (clojure.string/split s #"\n\n")]
    (into {}
          (map
           (fn [dir]
             (let [[k & vs] (clojure.string/split dir #"\n")]
               [(apply str (butlast (rest k))) (into {} (map read-values vs))])) dirs))))

(def-serial dump
  "Read a dconf path into :out"
  [k f]
  (sh "/usr/bin/dconf" "dump" k))

(comment
  (println (spit-conf (slurp-conf (:out (dump "/org/mate/terminal/" "/tmp/foo")))))
  (load  "/org/mate/terminal/keybindings/" {"/" {:select-all "disabled"}})
  (write "/org/mate/terminal/profiles/default/allow-bold" "false")
  (read "/org/mate/terminal/profiles/default/allow-bold")
  (load "/org/mate/terminal/profiles/default/" "false")
  (read "/org/mate/terminal/profiles/default/allow-bold"))
