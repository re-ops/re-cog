(ns re-cog.recipes.backup
  (:require
   [re-cog.resources.exec :refer [run]]
   [re-cog.common.recipe :refer (require-recipe)]
   [re-cog.resources.download :refer (download)]
   [re-cog.resources.file :refer (rename symlink chmod)]
   [re-cog.resources.archive :refer (untar bzip2)]
   [re-cog.resources.permissions :refer (set-file-acl)]))

(require-recipe)

(def-inline restic
  "Setting up restic"
  []
  (let [version "0.9.1"
        release (<< "restic_~{version}_linux_amd64")
        tmp (<< "/tmp/~{release}.bz2")
        expected "f7f76812fa26ca390029216d1378e5504f18ba5dde790878dfaa84afef29bda7"
        url (<< "https://github.com/restic/restic/releases/download/v~{version}/~{release}.bz2")]
    (download url tmp expected)
    (bzip2 tmp)
    (set-file-acl "re-ops" "rwX" "/usr/bin/")
    (rename (<< "/tmp/~{release}") "/usr/bin/restic")
    (chmod "/usr/bin/restic" "0755")))

(def-inline octo
  "Setting up octo"
  []
  (let [version "0.8.1"
        tmp "/tmp/octo"
        expected "c53abdfd81fc5eb48ff138faf3cdcd11acd7a089a44d0d82c05a63a56ef691ee"
        url (<< "https://github.com/narkisr/octo/releases/download/~{version}/octo")]
    (download url tmp expected)
    (set-file-acl "re-ops" "rwX" "/usr/bin/")
    (rename tmp "/usr/bin/octo")
    (chmod "/usr/bin/octo" "0755")))

(def-inline zbackup
  "Setting up zbackup"
  []
  (package "zbackup" :present))

(def-inline rclone
  "Setting up rclone"
  []
  (package "rclone" :present))

#_(defn dropbox
    [{:keys [home]}]
    (if (desktop?)
      (dropbox-desktop)
      (dropbox-headless home)))
