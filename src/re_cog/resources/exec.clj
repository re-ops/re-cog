(ns re-cog.resources.exec
  (:require
   [me.raynes.fs :as fs]
   [clojure.java.shell :refer [sh]]
   [re-cog.scripts.common :refer (shell-args)]
   [re-cog.common.defs :refer (def-serial)]))

; shell
(def-serial shell
  "Excute a script using bash"
  [sum script]
  (let [f (fs/file (fs/tmpdir) sum)]
    (when-not (fs/exists? f)
      (spit f script))
    (sh "bash" (.getPath f))))

(defn run
  "A local version of shell function"
  [script]
  (apply shell (shell-args script)))
