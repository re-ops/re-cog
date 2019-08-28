(ns re-cog.recipes.nvim
  "Setting up NeoVim"
  (:require
   [re-cog.facts.config :refer (configuration)]
   [re-cog.resources.download :refer (download)]
   [re-cog.resources.file :refer (symlink directory chmod)]
   [re-cog.resources.archive :refer (untar)]
   [re-cog.common.functions :refer (require-functions require-resources)]
   [re-cog.resources.permissions :refer (set-file-acl)]
   [re-cog.common :refer (require-constants)]
   [re-cog.common.defs :refer (def-inline)]))

(require-functions)
(require-resources)
(require-constants)

(def-inline nvim
  "Installing Neovim"
  []
  (let [version "0.3.8"
        archive "nvim-linux64.tar.gz"
        url (<< "https://github.com/neovim/neovim/releases/download/v~{version}/~{archive}")
        sum "30440b0eb4eb0adbf09c458891e81880c9efe5245b52316e9af1c08136088e6a"
        dest (<< "/usr/src/~{archive}")]
    (set-file-acl "re-ops" "rwX" "/usr/src")
    (download url dest sum)
    (set-file-acl "re-ops" "rwX" "/opt")
    (untar dest "/opt/")
    (set-file-acl "re-ops" "rwX" "/usr/local/bin/")
    (symlink "/usr/local/bin/nvim" "/opt/nvim-linux64/bin/nvim")))

(def-inline nodejs-support
  "nodejs neovim support"
  []
  (let [{:keys [home user]} (configuration)
        prefix (<< "/home/~{user}/.npm")
        npmrc (<< "~{home}/.npmrc")]
    (letfn [(npm-install [prefix pkg]
              (fn []
                (script ("/usr/bin/npm" "install" "--prefix" ~prefix ~pkg))))]
      (package "npm" :present)
      (file npmrc :present)
      (line npmrc (<< "prefix = ~{prefix}") :present)
      (run (npm-install prefix "neovim"))
      (run (npm-install prefix "node-cljfmt"))
      (directory (<< "~{home}/bin") :present)
      (symlink (<< "~{home}/bin/cljfmt") (<< "~{prefix}/node_modules/node-cljfmt/bin/cljfmt")))))

(def-inline config
  "Configure nvim"
  []
  (let [{:keys [home user]} (configuration)
        config (<< "~{home}/.config/nvim")]
    (clone "git://github.com/narkisr/nvim.git" config)
    (chown config user user {:recursive true})))

(def-inline powerline
  "Install powerline"
  []
  (let [{:keys [home user]} (configuration)
        fonts (<< "~{home}/.fonts")
        repo "git://github.com/scotu/ubuntu-mono-powerline.git"]
    (directory fonts :present)
    (clone repo (<< "~{fonts}/ubuntu-mono-powerline"))
    (chown fonts user user {:recursive true})))
