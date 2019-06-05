(ns re-cog.resources.package
  (:require [clojure.java.shell :refer [sh]]
            [re-share.oshi :refer (read-metrics os get-processes)]
            [re-cog.resources.common :refer (defn)]))

(defn pkg-update
  "update package manager"
  []
  (case (os)
    :Ubuntu (sh "sudo" "apt" "update")
    :FreeBSD (sh "sudo" "pkg" "update")
    (throw (ex-info "not supported" {:os (os)}))))

(defn pkg-upgrade
  "upgrade all packages"
  []
  (case (os)
    :Ubuntu (sh "sudo" "apt" "upgrade" "-y")
    :FreeBSD (sh "sudo" "pkg" "upgrade" "-y")
    (throw (ex-info "not supported" {:os (os)}))))

(defn pkg-install
  "install a package"
  [pkg]
  (case (os)
    :Ubuntu (sh "sudo" "apt" "install" pkg "-y")
    :FreeBSD (sh "sudo" "pkg" "install" pkg "-y")
    (throw (ex-info "not supported" {:os (os)}))))

(defn pkg-fix
  "Fix package provider"
  []
  (case (os)
    :Ubuntu (sh "sudo" "rm" "/var/lib/dpkg/lock" "/var/cache/apt/archives/lock")
    (throw (ex-info "not supported" {:os (os)}))))

(defn pkg-kill
  "kill package provider"
  []
  (case (os)
    :Ubuntu (sh "sudo" "killall" "apt")
    (throw (ex-info "not supported" {:os (os)}))))

