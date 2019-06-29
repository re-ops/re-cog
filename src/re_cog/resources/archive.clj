(ns re-cog.resources.archive
  "Archive resource support"
  (:require
   [re-cog.resources.package :refer (package)]
   [re-cog.common :refer (def-serial)]
   [me.raynes.fs :as fs]
   [re-cog.common.functions :refer (sh!)]))

(def-serial unzip
  "Unzip resource:
   
    (unzip \"foo.zip\" \"/tmp/foo\")
  "
  [src dest]
  (do
    (when-not (fs/exists? "/usr/bin/unzip")
      (re-cog.resources.package/package "unzip" :present))
    (sh! "/usr/bin/unzip" "-o" src "-d" dest)))

(def-serial bzip2
  "bzip2 extraction resource:
   
    (bzip2 \"foo.bz2\")
  "
  [target]
  (assert (fs/exists? "/usr/bin/unzip"))
  (sh! "/bin/bzip2" "-kf" "-d" target))

(def-serial untar
  "Untar resource:

    (untar \"foo.tar\" \"/tmp/foo\")
  "
  [src dest]
  (sh! "/bin/tar" "-xzf" src "-C" dest))
