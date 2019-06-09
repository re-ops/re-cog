(ns re-cog.resources.exec
  (:require
   [re-cog.common :refer (require-functions def-serial)]))

(require-functions)

; shell
(defn shell
  "Excute a script using bash"
  [sum script]
  (let [f (file (tmpdir) sum)]
    (when-not (exists? f)
      (spit f script))
    (sh "bash" (.getPath f))))

