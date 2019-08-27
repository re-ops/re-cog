(ns re-cog.recipes.shell
  "Setting up shell"
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

(def-inline tmux
  "Setup tmux for user"
  []
  (let [{:keys [home name]} (configuration)
        dest (<< "~{home}/.tmux")]
    (package "tmux" :present)
    (clone "git://github.com/narkisr/.tmux.git" dest)
    (directory (<< "~{dest}/plugins/") :present)
    (clone "git://github.com/tmux-plugins/tpm" (<< "~{dest}/plugins/tpm"))
    (symlink (<< "~{home}/.tmux.conf") (<< "~{dest}/.tmux.conf"))
    (chown dest name name {})
    (clone "git://github.com/narkisr/.tmuxinator.git" (<< "~{home}/.tmuxinator.git"))
    (chown (<< "~{home}/.tmuxinator.git") name name {:recursive true})))

(def-inline zsh
  "zsh setup"
  []
  (letfn [(chsh [name]
            (fn []
              (script "/usr/bin/chsh" "-s" "/usr/bin/zsh" ~name)))]
    (let [{:keys [home name]} (configuration)
          dest (<< "~{home}/.tmux")]
      (package "zsh" :present)
      (when-not  (clojure.string/includes? (<< "~{name}:/bin/zsh") (slurp "/etc/passwd"))
        (run (chsh name))))))

(def-inline oh-my-zsh
  "Setup https://github.com/robbyrussell/oh-my-zsh"
  []
  (let [{:keys [home name]} (configuration)
        dest (<< "~{home}/.oh-my-zsh")]
    (clone "git://github.com/narkisr/oh-my-zsh.git" dest)
    (chown dest name name {})
    (symlink (<< "~{home}/.zshrc") (<< "~{dest}/.zshrc"))))

(def-inline dot-files
  "Setting up dot files from git://github.com/narkisr/dots.git"
  []
  (let [{:keys [home name]} (configuration)
        dest (<< "~{home}/.dots")]
    (clone "git://github.com/narkisr/dots.git" dest)
    (chown dest name name {})))

(def-inline ack
  "ack grep setup"
  []
  (let [{:keys [home]} (configuration)
        dots (<< "~{home}/.dots")]
    (package "ack" :present)
    (symlink (<< "~{home}/.ackrc") (<< "~{dots}/.ackrc"))))

(def-inline rlwrap
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
  (let [version "0.11.0"
        artifact (<< "bat_~{version}_amd64.deb")
        url (<< "https://github.com/sharkdp/bat/releases/download/v~{version}/~{artifact}")
        sum "35469591eb030d901a915bd161aeab2859685038de797270a0fa1290ef45255b"]
    (download url (<< "/tmp/~{artifact}") sum)
    (package (<< "/tmp/~{artifact}") :present)))