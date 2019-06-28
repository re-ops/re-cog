(ns re-cog.resources.package
  (:require
   [pallet.stevedore :refer (script)]
   [re-cog.resources.exec :refer (run)]
   [re-cog.resources.download :refer (download)]
   [re-cog.common :refer (require-functions def-serial def-inline require-constants)]))

(require-functions)
(require-constants)

(def-serial installed?
  "Check is package is installed"
  [pkg]
  (case (os)
    :Ubuntu (sh! "sudo" "/usr/bin/dpkg" "-s" pkg)
    :default (throw (ex-info (<< "No matching package provider found for ~(os)") {}))))

; consumers

(def-serial package
  "Package resource with optional provider and state parameters:
    (package \"ghc\" :present)
    (package \"ghc\" \"gnome-terminal\" :present) ; multiple packages
    (package \"/tmp/foo.deb\" deb :present) ; using deb provider
    (package \"ghc\" :absent) ; remove package
  "
  [pkg state]
  (letfn [(install [pkg]
            (if (.endsWith pkg "deb")
              (sh! "sudo" dpkg-bin "-i" pkg)
              (sh! "sudo" apt-bin "install" pkg "-y")))
          (uninstall [pkg]
                     (if (.endsWith pkg "deb")
                       (sh! "sudo" dpkg-bin "-r" pkg)
                       (sh! "sudo" apt-bin "remove" pkg "-y")))]
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
  (letfn [(add-repo [repo] (sh! "/usr/bin/add-apt-repository" repo "-y"))
          (rm-repo [repo] (sh! "/usr/bin/add-apt-repository" "--remove" repo "-y"))]
    (let [fns {:present add-repo :absent rm-repo}]
      ((fns state) repo))))

(def-serial key-file
  "Import a gpg apt key from a file resource:
     (key-file \"key.gpg\")
   "
  [file]
  (case (os)
    :Ubuntu (sh! "/usr/bin/apt-key" "add" file)
    :default (throw (ex-info (<< "cant import apt key under os ~(os)") {}))))

(def-serial key-server
  "Import a gpg apt key from a gpg server resource:
     (key-server \"keyserver.ubuntu.com\" \"42ED3C30B8C9F76BC85AC1EC8B095396E29035F0\")
   "
  [server id]
  (case (os)
    :Ubuntu (sh! "/usr/bin/apt-key" "adv" "--keyserver" server "--recv" id)
    :default (throw (ex-info (<< "cant import apt key under ~(os)") {}))))

(def-inline add-repo
  "Add repo, gpg key and fingerprint in one go."
  [repo url id]
  (download url (<< "/tmp/~{id}.key"))
  (key-file (<< "/tmp/~{id}.key"))
  ;; (fingerprint id)
  (repository repo :present)
  (update-))
