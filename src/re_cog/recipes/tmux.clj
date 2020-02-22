(ns re-cog.recipes.tmux
  "Setting up tmux and related utilities"
  (:require
   [re-cog.resources.git :refer (clone)]
   [re-cog.resources.exec :refer [run]]
   [re-cog.common.recipe :refer (require-recipe)]
   [re-cog.facts.config :refer (configuration)]
   [re-cog.resources.download :refer (download)]
   [re-cog.resources.file :refer (symlink directory chmod)]))

(require-recipe)

(def-inline tmux
  "Setup tmux for user"
  []
  (let [{:keys [home user]} (configuration)
        dest (<< "~{home}/.tmux")]
    (package "tmux" :present)
    (clone "git://github.com/narkisr/.tmux.git" dest)
    (directory (<< "~{dest}/plugins/") :present)
    (clone "git://github.com/tmux-plugins/tpm" (<< "~{dest}/plugins/tpm"))
    (symlink (<< "~{home}/.tmux.conf") (<< "~{dest}/.tmux.conf"))
    (chown dest user user {})))

(def-inline {:depends #'re-cog.recipes.access/permissions} tmx
  "Setting up tmx https://github.com/narkisr/tmx"
  []
  (let [version "0.2.2"
        sum "022b3bc09a4315b462ae419f043f54ca200a0666baef2cc5f81d1d4e07fb6615"
        url (<< "https://github.com/narkisr/tmx/releases/download/~{version}/tmx")
        dest "/usr/local/bin/tmx"]
    (download url dest sum)
    (chmod dest "+x")))
