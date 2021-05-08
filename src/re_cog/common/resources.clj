(ns re-cog.common.resources
  "Common functions that are used within resources"
  (:require
   [me.raynes.fs :as fs]
   [clojure.java.shell :refer [sh]]
   [re-cog.scripts.common :refer (shell-args bash-path)]))

(defn run-
  "Excute a script generated from a function using bash (intended to be used from other resource functions)
     Note: output should be verifired unelss last statement in resource!"
  [script-fn]
  (let [[sum forced! _] (shell-args script-fn)
        f (fs/file (fs/tmpdir) sum)]
    (try
      (when-not (fs/exists? f)
        (spit f forced!))
      (sh (bash-path) (.getPath f))
      (finally
        (.delete f)))))

(defn run!-
  "Excute a script and throw an exception if failing"
  [script-fn]
  (let [{:keys [exit] :as res} (run- script-fn)]
    (if (= 0 exit)
      res
      (throw (ex-info "failed to run script" res)))))
