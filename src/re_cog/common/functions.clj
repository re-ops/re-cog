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

(defn coherce
  ([b]
   (coherce b "" ""))
  ([b err]
   (coherce b err ""))
  ([b err out]
   {:exit (if b 0 1) :out out :err err}))

(defmacro success [out]
  (coherce true "" out))

(defn failure [err]
  (coherce false err ""))

(defn require-resources
  "Requiring common resources"
  []
  (require
   '[re-cog.resources.package :refer [package repository]]
   '[re-cog.resources.git :refer [clone pull repo-exists? binary install-missing]]
   '[re-cog.resources.file :refer [chown file directory symlink line line-set]]
   '[re-cog.resources.disk :refer [partition- mount]]
   '[re-cog.resources.exec :refer [run]]))

(defn require-functions
  "Require common resource functions in order to suppoert inlined functions"
  []
  (require
   ; scripting
   '[clojure.java.shell :refer [sh]]
   '[pallet.stevedore :refer (script)]
   '[pallet.stevedore.bash]
   '[re-cog.common.functions :refer [sh! file-checksum coherce success failure]]
   '[re-cog.scripts.common :refer [shell-args bash-path]]
   ; profiling
   '[re-share.core :refer [measure gen-uuid]]
   ; templating
   '[cljstache.core :refer [render]]
   ; io
   '[digest :as digest]
   '[clojure.java.io :as io]
   '[me.raynes.fs :as fs :refer (list-dir tmpdir exists?)]
   ; datalog facts
   '[re-cog.facts.datalog :refer (os ubuntu-desktop? ubuntu-version verify query singleton get-db fqdn hostname)]
   '[datascript.core :as d]
   ; common
   '[re-cog.facts.config :refer (configuration)]
   '[taoensso.timbre :refer (info error debug trace)]
   '[clojure.core.strint :refer (<<)]))
