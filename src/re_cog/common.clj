(ns re-cog.common "Common resource functions"
    (:require
     [clojure.repl]
     [serializable.fn :as s]))

(defn into-spec [m args]
  (if (empty? args)
    m
    (let [a (first args)]
      (cond
        (or (fn? a) (string? a)) (into-spec (clojure.core/update m :args (fn [v] (conj v a))) (rest args))
        (keyword? a) (into-spec (assoc m :state a) (rest args))))))

(defmacro def-serial
  "Define a serializable function"
  ([name doc args body]
   `(def ^{:doc ~doc} ~name (s/fn ~args ~body)))
  ([name doc args pre-post body]
   `(def ^{:doc ~doc :prepost pre-post} ~name (s/fn ~args ~body))))

(defn source-list [f]
  (let [[_ name _ args body] (read-string (clojure.repl/source-fn (first f)))]
    (list name args body)))

(defn letfn- [body]
  (into [] (map source-list body)))

(defmacro def-inline
  "Serialized function with inlined body functions"
  [name doc args & body]
  (let [letform (letfn- body)]
    `(def-serial ~name ~doc ~args (letfn ~letform ~@body))))

(defn sh!
  "Run sh throws exception if exit code != 0"
  [& args]
  (let [{:keys [err exit] :as m} (apply clojure.java.shell/sh args)]
    (if (not (= exit 0))
      (throw (ex-info err m))
      m)))

(defn require-functions
  "Require common resource functions"
  []
  (require
   '[clojure.java.shell :refer [sh]]
   '[re-cog.common :refer [sh!]]
   '[re-share.oshi :refer (read-metrics os get-processes)]
   '[clojure.core.strint :refer (<<)]
   '[digest :as digest]
   '[me.raynes.fs :as fs :refer (list-dir tmpdir exists?)]
   '[taoensso.timbre :refer (info error debug trace)]
   '[clojure.java.io :as io]))

; Constants
(def apt-bin "/usr/bin/apt-get")

(def dpkg-bin "/usr/bin/dpkg")

(defn require-constants
  "Require common resource functions"
  []
  (require
   '[re-cog.common :refer [apt-bin dpkg-bin]]))
