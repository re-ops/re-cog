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
    (symlink "/usr/local/bin/nvim" "/opt/nvim-linux64/bin/nvim")))

(def-inline nodejs-support
  "nodejs neovim support"
  []
  (let [{:keys [home name]} (configuration)
        prefix (<< "/home/~{name}/.npm")
        npmrc (<< "~{home}/.npmrc")]
    (letfn [(npm-install [pkg]
              (fn []
                (script ("/usr/bin/npm" "install" "--prefix" ~prefix ~pkg))))]
      (package "npm" :present)
      (file npmrc :present)
      (line npmrc (<< "prefix = ~{prefix}") :present)
      (run (npm-install "neovim"))
      (run (npm-install "node-cljfmt"))
      (directory (<< "~{home}/bin") :present)
      (symlink (<< "~{home}/bin/cljfmt") (<< "~{prefix}/node_modules/node-cljfmt/bin/cljfmt")))))

(def-inline config
  "Configure nvim"
  []
  (let [{:keys [home name]} (configuration)
        config (<< "~{home}/.config/nvim")]
    (clone "git://github.com/narkisr/nvim.git" config)
    (chown config name name {:recursive true})))

(def-inline powerline
  "Install powerline"
  []
  (let [{:keys [home name]} (configuration)
        fonts (<< "~{home}/.fonts")
        repo "git://github.com/scotu/ubuntu-mono-powerline.git"]
    (directory fonts :present)
    (clone repo (<< "~{fonts}/ubuntu-mono-powerline"))
    (chown fonts name name {:recursive true})))
