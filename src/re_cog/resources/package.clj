(ns re-cog.resources.package
  (:require [clojure.java.shell :refer [sh]]
            [re-share.oshi :refer (read-metrics os get-processes)]
            [serializable.fn :as s]))

(def ^{:doc "update package manager"} pkg-update
  (s/fn []
    (case (os)
      :Ubuntu (sh "sudo" "apt" "update")
      :FreeBSD (sh "sudo" "pkg" "update")
      (throw (ex-info "not supported" {:os (os)})))))

(def ^{:doc "upgrade all packages"} pkg-upgrade
  (s/fn []
    (case (os)
      :Ubuntu (sh "sudo" "apt" "upgrade" "-y")
      :FreeBSD (sh "sudo" "pkg" "upgrade" "-y")
      (throw (ex-info "not supported" {:os (os)})))))

(def ^{:doc "install a package"} pkg-install
  (s/fn [pkg]
    (case (os)
      :Ubuntu (sh "sudo" "apt" "install" pkg "-y")
      :FreeBSD (sh "sudo" "pkg" "install" pkg "-y")
      (throw (ex-info "not supported" {:os (os)})))))

(def ^{:doc "Fix package provider"} pkg-fix
  (s/fn []
    (case (os)
      :Ubuntu (sh "sudo" "rm" "/var/lib/dpkg/lock" "/var/cache/apt/archives/lock")
      (throw (ex-info "not supported" {:os (os)})))))

(def ^{:doc "kill package provider"} pkg-kill
  (s/fn []
    (case (os)
      :Ubuntu (sh "sudo" "killall" "apt")
      (throw (ex-info "not supported" {:os (os)})))))

