(ns re-cog.resources.package
  (:require
   [re-cog.resources.download :refer (download)]
   [re-cog.resources.exec :refer (run)]
   [re-cog.common.functions :refer (require-functions)]
   [re-cog.common.defs :refer (def-serial def-inline)]
   [re-cog.common.constants :refer (require-constants)]))

(require-functions)
(require-constants)

(def-serial installed?
  "Check is package is installed"
  [pkg]
  (case (os)
    :Ubuntu (sh "sudo" "/usr/bin/dpkg" "-s" pkg)
    :default (throw (ex-info (<< "No matching package provider found for ~(os)") {}))))

; consumers

(def-serial package
  "Package resource with optional provider and state parameters:
    (package \"ghc\" :present)
    (package \"/tmp/foo.deb\" deb :present) ; using deb provider
    (package \"ghc\" :absent) ; remove package
  "
  [pkg state]
  (letfn [(install [pkg]
            (if (.endsWith pkg "deb")
              (sh "sudo" "/usr/bin/env" "DEBIAN_FRONTEND=noninteractive"  dpkg-bin "-i" pkg)
              (sh "sudo" "/usr/bin/env" "DEBIAN_FRONTEND=noninteractive" apt-bin "install" pkg "-y")))
          (uninstall [pkg]
                     (if (.endsWith pkg "deb")
                       (sh "sudo" dpkg-bin "-r" pkg)
                       (sh "sudo" apt-bin "remove" pkg "-y")))]
    (let [fns {:present install :absent uninstall}]
      (debug "installing")
      ((fns state) pkg))))

(def-serial update-
  "Update package repository index resource:
    (update)"
  []
  (letfn [(update-script []
            (script ("sudo" ~apt-bin "update")))]
    (run update-script)))

(def-serial upgrade
  "Upgrade installed packages:
    (upgrade)
  "
  []
  (letfn [(upgrade-script []
            (script ("sudo" ~apt-bin "upgrade" "-y")))]
    (run upgrade-script)))

(def-serial repository
  "Package repository resource:
    (repository \"deb https://raw.githubusercontent.com/narkisr/fpm-barbecue/repo/packages/ubuntu/ xenial main\" :present)
    (repository \"deb https://raw.githubusercontent.com/narkisr/fpm-barbecue/repo/packages/ubuntu/ xenial main\" :absent)
    (repository \"ppa:neovim-ppa/stable\" :present)
    (repository \"ppa:neovim-ppa/stable\" :absent)
   "
  [repo state]
  (letfn [(add-repo [repo] (sh "sudo" "/usr/bin/add-apt-repository" repo "-y"))
          (rm-repo [repo] (sh "sudo" "/usr/bin/add-apt-repository" "--remove" repo "-y"))]
    (let [fns {:present add-repo :absent rm-repo}]
      ((fns state) repo))))

(def-serial key-file
  "Import a gpg apt key from a file resource:
     (key-file \"key.gpg\")
   "
  [file]
  (case (os)
    :Ubuntu (sh "sudo" "/usr/bin/apt-key" "add" file)
    :default (throw (ex-info (<< "cant import apt key under os ~(os)") {}))))

(def-serial key-server
  "Import a gpg apt key from a gpg server resource:
     (key-server \"keyserver.ubuntu.com\" \"42ED3C30B8C9F76BC85AC1EC8B095396E29035F0\")
   "
  [server id]
  (case (os)
    :Ubuntu (sh "sudo" "/usr/bin/apt-key" "adv" "--keyserver" server "--recv" id)
    :default (throw (ex-info (<< "cant import apt key under ~(os)") {}))))

(def-serial fingerprint
  "Verify a apt-key gpg key signature with id"
  [id]
  (sh "/usr/bin/apt-key" "fingerprint" id))

