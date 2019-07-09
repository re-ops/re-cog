(ns re-cog.resources.git
  "Git resources"
  (:require
   [re-cog.common :refer (def-inline def-serial)]
   [clojure.core.strint :refer (<<)]
   [re-cog.common.functions :refer (require-functions require-resources)]
   [clojure.string :refer (includes?)]))

(require-functions)
(require-resources)

(def-serial binary
  "Grab git binary path"
  []
  (case (os)
    :Ubuntu "/usr/bin/git"
    :FreeBSD "/usr/local/bin/git"
    :default (throw (ex-info (<< "No matching git path found for ~(os)") {}))))

(def-serial repo-exists?
  "check if repo exists"
  [repo path]
  (when (fs/exists? (<< "~{path}/.git/config"))
    (clojure.string/includes? (slurp (<< "~{path}/.git/config")) repo)))

(def-inline pull
  "Pull implementation"
  [repo dest]
  (if (repo-exists? repo dest)
    (let [git (binary)
          dir (<< "--git-dir=~{dest}.git")]
      (run (fn [] (script (~git ~dir "pull")))))
    (<< "Skipping pull ~{repo} is missing under ~{dest}")))

(def-inline clone
  "Clone implementation"
  [repo dest]
  (letfn [(clone-script []
            (let [git (binary)]
              (script (~git "clone" ~repo ~dest))))]
    (if-not (repo-exists? repo dest)
      (run clone-script)
      {:out (<< "Skipping clone ~{repo} exists under ~{dest}")})))
