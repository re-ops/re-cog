(ns re-cog.recipes.nvim
  "Setting up NeoVim"
  (:require
   [re-cog.common.functions :refer (require-functions require-resources)]
   [re-cog.common :refer (require-constants)]
   [re-cog.common.defs :refer (def-inline)]))

(require-functions)
(require-resources)
(require-constants)

(def-inline nvim
  "Installing Neovim"
  []
  (repository "ppa:neovim-ppa/unstable" :present)
  (package "neovim" :present))

(def-inline nodejs-support
  "nodejs neovim support"
  [{:keys [home name]}]
  (let [prefix (<< "/home/~{name}/.npm")
        npmrc (<< "~{home}/.npmrc")]
    (letfn [(npm-install [pkg]
              (fn []
                (script ("/usr/bin/npm" "install" "--prefix" ~prefix ~pkg))))]
      (package "npm" :present)
      (run (npm-install "neovim"))
      (run (npm-install "node-cljfmt"))
      (file npmrc :present)
      (line npmrc (<< "prefix = ~{prefix}") :present)
      (directory (<< "~{home}/bin") :present)
      (symlink (<< "~{home}/bin/cljfmt") (<< "~{prefix}/node_modules/node-cljfmt/bin/cljfmt")))))

(def-inline python-support
  "Neovim python support"
  [{:keys [home name]}]
  (letfn [(pip-install []
            (script ("/usr/bin/pip3" "install" "--user" "neovim")))]
    (package "python3-pip" :present)
    (package "python3-dev" :present)
    (package "python-pip" :present)
    (package "python-dev" :present)
    (run pip-install)
    (chown (<< "~{home}/.local") name name {:recursive true})))

(def-inline ruby-support
  "nvim ruby support"
  []
  (letfn [(gem-install []
            (script ("sudo" "/usr/bin/gem" "install" "neovim")))]
    (package "rubygems" :present)
    (package "ruby2.5-dev" :present)
    (run gem-install)))

(def-inline config
  "Configure nvim"
  [{:keys [home name]}]
  (let [config (<< "~{home}/.config/nvim")]
    (clone "git://github.com/narkisr/nvim.git" config)
    (chown config name name {:recursive true})))

(def-inline powerline
  "Install powerline"
  [{:keys [home name]}]
  (let [fonts (<< "~{home}/.fonts")
        repo "git://github.com/scotu/ubuntu-mono-powerline.git"]
    (directory fonts :present)
    (clone repo (<< "~{fonts}/ubuntu-mono-powerline"))
    (chown fonts name name {:recursive true})))
