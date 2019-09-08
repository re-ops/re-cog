(ns re-cog.resources.git
  "Git resources"
  (:require
   [re-cog.common.defs :refer (def-inline def-serial)]
   [clojure.core.strint :refer (<<)]
   [re-cog.common.functions :refer (require-functions require-resources)]
   [re-cog.resources.exec :refer (run)]
   [re-cog.common :refer (require-constants)]
   [clojure.string :refer (includes?)]))

(require-functions)

(def-serial binary
  "Grab git binary path"
  []
  (case (os)
    :Ubuntu "/usr/bin/git"
    :FreeBSD "/usr/local/bin/git"
    :default (throw (ex-info (<< "No matching git path found for ~(os)") {}))))

(def-serial install-missing
  "Installing git if missing"
  [bin]
  (when-not (fs/exists? bin)
    (re-cog.resources.package/package "git" :present)))

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
