(ns re-cog.resources.exec
  (:require
   [me.raynes.fs :as fs]
   [clojure.java.shell :refer [sh]]
   [re-cog.scripts.common :refer (shell-args bash-path)]
   [re-cog.common.defs :refer (def-serial)]))

; shell
(def-serial shell
  "Execute a script remotly using bash (script is computed before hand)"
  [sum script & {:keys [cached?] :or {cached? false}}]
  (let [f (fs/file (fs/tmpdir) sum)]
    (try
      (when-not (fs/exists? f)
        (spit f script))
      (sh (bash-path) (.getPath f))
      (finally
        (when-not cached?
          (.delete f))))))

(def-serial run
  "Excute a script using bash (script-fn is in scope)"
  [script-fn & {:keys [cached?] :or {cached? false}}]
  (let [[sum forced! _] (shell-args script-fn)
        f (fs/file (fs/tmpdir) sum)]
    (try
      (when-not (fs/exists? f)
        (spit f forced!))
      (sh (bash-path) (.getPath f))
      (finally
        (when-not cached?
          (.delete f))))))
