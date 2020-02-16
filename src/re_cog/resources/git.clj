(ns re-cog.resources.git
  "Git resources"
  (:require
   [re-cog.resources.package]
   [re-cog.common.defs :refer (def-serial)]
   [clojure.core.strint :refer (<<)]
   [re-cog.common.functions :refer (require-functions)]
   [re-cog.resources.exec :refer (run)]))

(require-functions)

(defn binary
  "Grab git binary path"
  []
  (case (os)
    :Ubuntu "/usr/bin/git"
    :FreeBSD "/usr/local/bin/git"
    :default (throw (ex-info (<< "No matching git path found for ~(os)") {}))))

(defn install-missing
  "Installing git if missing"
  [bin]
  (when-not (fs/exists? bin)
    (when-not (= 0 (:exit (re-cog.resources.package/package "git" :present)))
      (failure "failed to install git"))))

(def-serial repo-exists?
  "check if repo exists"
  [repo path]
  (if (fs/exists? (<< "~{path}/.git/config"))
    (coherce
     (clojure.string/includes?
      (slurp (<< "~{path}/.git/config")) repo))
    (failure "repository is missing")))

(def-serial pull
  "Pull implementation"
  [repo dest]
  (if (= 0 (:exit (repo-exists? repo dest)))
    (let [git (binary)
          dir (<< "--git-dir=~{dest}.git")]
      (install-missing git)
      (run (fn [] (script (~git ~dir "pull")))))
    (failure (<< "Skipping pull remote ~{repo} is no found under ~{dest}"))))

(def-serial clone
  "Clone implementation"
  [repo dest]
  (let [git (binary)]
    (install-missing git)
    (letfn [(clone-script []
              (script (~git "clone" ~repo ~dest)))]
      (if-not (= 0 (:exit (repo-exists? repo dest)))
        (run clone-script)
        (success (<< "Skipping clone ~{repo} exists under ~{dest}"))))))
