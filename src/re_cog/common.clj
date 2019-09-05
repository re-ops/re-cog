(ns re-cog.common
  "Common resource functions"
  (:require
   [pallet.stevedore]))

(defn into-spec [m args]
  (if (empty? args)
    m
    (let [a (first args)]
      (cond
        (or (fn? a) (string? a)) (into-spec (clojure.core/update m :args (fn [v] (conj v a))) (rest args))
        (keyword? a) (into-spec (assoc m :state a) (rest args))))))

(defn bind-bash
  "Bind stevedore language to bash"
  []
  (.bindRoot (var pallet.stevedore/*script-language*) :pallet.stevedore.bash/bash))

; Constants

(def apt-bin "/usr/bin/apt")

(def dpkg-bin "/usr/bin/dpkg")

(def systemd-bin "/usr/sbin/service")

(def systemctl-bin "/bin/systemctl")

(defn require-constants
  "Require common constant values"
  []
  (require
   '[re-cog.common :refer [apt-bin dpkg-bin systemd-bin systemctl-bin]]))
