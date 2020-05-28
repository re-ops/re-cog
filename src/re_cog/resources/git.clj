(ns re-cog.resources.git
  "Git resources"
  (:require
   [re-cog.resources.package]
   [re-cog.common.defs :refer (def-serial)]
   [clojure.core.strint :refer (<<)]
   [re-cog.common.functions :refer (require-functions)]
   [re-cog.common.resources :refer (run-)]))

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
  (if-not (fs/exists? bin)
    (re-cog.resources.package/package "git" :present)
    {:exit 0 :noop true}))

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
  (let [git (binary)
        {:keys [exit] :as binary-install} (install-missing git)]
    (if-not (= 0 exit)
      binary-install
      (if (= 0 (:exit (repo-exists? repo dest)))
        (let [dir (<< "--git-dir=~{dest}.git")]
          (run- (fn [] (script (~git ~dir "pull")))))
        (failure (<< "Skipping pull remote ~{repo} is no found under ~{dest}"))))))

(def-serial clone
  "Clone implementation"
  [repo dest]
  (let [git (binary)
        {:keys [exit] :as binary-install} (install-missing git)]
    (if-not (= 0 exit)
      binary-install
      (letfn [(clone-script []
                (script (~git "clone" ~repo ~dest)))]
        (if-not (= 0 (:exit (repo-exists? repo dest)))
          (run- clone-script)
          (success (<< "Skipping clone ~{repo} exists under ~{dest}")))))))
