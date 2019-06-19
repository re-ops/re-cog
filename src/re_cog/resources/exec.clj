(ns re-cog.resources.exec
  (:require
   [re-cog.scripts.common :refer (shell-args)]
   [re-cog.common :refer (require-functions def-serial)]))

(require-functions)

; shell
(def-serial shell
  "Excute a script using bash"
  [sum script]
  (let [f (fs/file (tmpdir) sum)]
    (when-not (fs/exists? f)
      (spit f script))
    (sh "bash" (.getPath f))))

(defn run
  "A local version of shell function"
  [script]
  (apply))
