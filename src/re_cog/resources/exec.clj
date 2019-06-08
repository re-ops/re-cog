(ns re-cog.resources.exec
  (:require
   [clojure.java.shell :refer [sh]]
   [me.raynes.fs :refer (list-dir tmpdir exists? file)]
   [re-cog.common :refer (defn)]))

; shell
(defn shell
  "Excute a script using bash"
  [sum script]
  (let [f (file (tmpdir) sum)]
    (when-not (exists? f)
      (spit f script))
    (sh "bash" (.getPath f))))

