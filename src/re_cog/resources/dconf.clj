(ns re-cog.resources.dconf
  "Linux Dconf resources"
  (:require
   [me.raynes.fs :as fs]
   [clojure.java.shell :refer (sh)]
   [re-cog.common.defs :refer (def-serial)]))

(def-serial write
  "Write a single value to dconf key"
  [k v]
  (sh "/usr/bin/dconf" "write" k v))

(def-serial read
  "Read a single value to dconf key"
  [k]
  (update (sh "/usr/bin/dconf" "read" k) :out clojure.string/trim))

(def-serial load
  "Populate a dconf subpath from stdin"
  [k f]
  (assert (fs/exists? f))
  (sh "/usr/bin/dconf" "load" k f))

(comment
  (write "/org/mate/terminal/profiles/default/allow-bold" "false")
  (load "/org/mate/terminal/profiles/default/" "false")
  (read "/org/mate/terminal/profiles/default/allow-bold"))
