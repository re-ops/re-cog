(ns re-cog.resources.archive
  "Archive resource support"
  (:require
   [clojure.java.shell :refer (sh)]
   [re-cog.resources.package]
   [re-cog.common.defs :refer (def-serial)]
   [me.raynes.fs :as fs]))

(def-serial unzip
  "Unzip resource:

    (unzip \"foo.zip\" \"/tmp/foo\")
  "
  [src dest]
  (do
    (when-not (fs/exists? "/usr/bin/unzip")
      (re-cog.resources.package/package "unzip" :present))
    (sh "/usr/bin/unzip" "-o" src "-d" dest)))

(def-serial bzip2
  "bzip2 extraction resource:
   
    (bzip2 \"foo.bz2\")
  "
  [target]
  (do
    (when-not (fs/exists? "/bin/bzip2")
      (re-cog.resources.package/package "bzip2" :present))
    (sh "/bin/bzip2" "-kf" "-d" target)))

(def-serial untar
  "Untar resource:

    (untar \"foo.tar\" \"/tmp/foo\")
  "
  [src dest]
  (sh "/bin/tar" "-xzf" src "-C" dest))
