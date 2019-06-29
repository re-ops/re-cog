(ns re-cog.common.functions
  "Common re-cog recipes functions"
  (:require
   [clojure.java.io :as io]
   [digest :as digest]
   clojure.java.shell))

(defn sh!
  "Run sh throws exception if exit code != 0"
  [& args]
  (let [{:keys [err exit] :as m} (apply clojure.java.shell/sh args)]
    (if (not (= exit 0))
      (throw (ex-info err m))
      m)))

(defn file-checksum
  [f {:keys [hash-type] :or {hash-type :sha256}}]
  (let [digest-fns {:sha256 digest/sha-256 :md5 digest/md5}]
    ((digest-fns hash-type) (io/as-file f))))

(defn require-resources
  "Requiring common resources"
  []
  (require
   '[re-cog.resources.package :refer [package]]
   '[re-cog.resources.exec :refer [run]]))

(defn require-functions
  "Require common resource functions"
  []
  (require
   ; scripting
   '[clojure.java.shell :refer [sh]]
   '[pallet.stevedore :refer (script)]
   '[pallet.stevedore.bash]
   '[re-cog.common.functions :refer [sh! file-checksum]]
   '[re-cog.scripts.common :refer [shell-args]]
   ; os info
   '[re-share.oshi :refer (read-metrics os get-processes)]
   '[re-share.core :refer [measure]]
   ; io
   '[digest :as digest]
   '[clojure.java.io :as io]
   '[me.raynes.fs :as fs :refer (list-dir tmpdir exists?)]
   ; common
   '[taoensso.timbre :refer (info error debug trace)]
   '[clojure.core.strint :refer (<<)]))
