(ns re-cog.recipes.shell
  "Setting up shell"
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
    (chown dest user user {})
    (clone "git://github.com/narkisr/.tmuxinator.git" (<< "~{home}/.tmuxinator.git"))
    (chown (<< "~{home}/.tmuxinator.git") user user {:recursive true})))

(def-inline zsh
  "zsh setup"
  []
  (letfn [(chsh [user]
            (fn []
              (script ("sudo" "/usr/bin/chsh" "-s" "/usr/bin/zsh" ~user))))]
    (let [{:keys [home user]} (configuration)
          dest (<< "~{home}/.tmux")]
      (package "zsh" :present)
      (when-not  (clojure.string/includes? (<< "~{user}:/bin/zsh") (slurp "/etc/passwd"))
        (run (chsh user))))))

(def-inline oh-my-zsh
  "Setup https://github.com/robbyrussell/oh-my-zsh"
  []
  (let [{:keys [home user]} (configuration)
        dest (<< "~{home}/.oh-my-zsh")]
    (clone "git://github.com/narkisr/oh-my-zsh.git" dest)
    (chown dest user user {})
    (symlink (<< "~{home}/.zshrc") (<< "~{dest}/.zshrc"))))

(def-inline dot-files
  "Setting up dot files from git://github.com/narkisr/dots.git"
  []
  (let [{:keys [home user]} (configuration)
        dest (<< "~{home}/.dots")]
    (clone "git://github.com/narkisr/dots.git" dest)
    (chown dest user user {})))

(def-inline {:depends #'re-cog.recipes.shell/dot-files} ack
  "ack grep setup"
  []
  (let [{:keys [home]} (configuration)
        dots (<< "~{home}/.dots")]
    (package "ack" :present)
    (symlink (<< "~{home}/.ackrc") (<< "~{dots}/.ackrc"))))

(def-inline {:depends #'re-cog.recipes.shell/dot-files} rlwrap
  "rlwrap setup"
  []
  (let [{:keys [home]} (configuration)
        dots (<< "~{home}/.dots")]
    (package "rlwrap" :present)
    (symlink  (<< "~{home}/.inputrc") (<< "~{dots}/.inputrc"))))

(def-inline fd
  "fd a friendly alternative to find"
  []
  (let [version "7.3.0"
        artifact (<< "fd_~{version}_amd64.deb")
        url (<< "https://github.com/sharkdp/fd/releases/download/v~{version}/~{artifact}")
        sum "2adddffeef7592cd8e87bff6a73a0b155bedbf4fe2a61ee3c2430ec384d9b478"]
    (download url (<< "/tmp/~{artifact}") sum)
    (package (<< "/tmp/~{artifact}") :present)))

(def-inline bat
  "bat a modern cat"
  []
  (let [version "0.12.1"
        artifact (<< "bat_~{version}_amd64.deb")
        url (<< "https://github.com/sharkdp/bat/releases/download/v~{version}/~{artifact}")
        sum "4e5502de4aeccb685bf326448afcaf99c3e4ae8e11b2756bdedcc66a095ba4e5"]
    (download url (<< "/tmp/~{artifact}") sum)
    (package (<< "/tmp/~{artifact}") :present)))
