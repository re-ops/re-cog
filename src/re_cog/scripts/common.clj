(ns re-cog.scripts.common
  (:require
   [clojure.java.shell :refer [sh]]
   [me.raynes.fs :as fs]
   [re-share.core :refer (md5)]
   [re-cog.facts.datalog :refer (os ubuntu-version)]
   [pallet.stevedore :refer (script do-script)]
   [re-cog.common.defs :refer (def-serial)]))

(def bash-path
  "Get the bash path for the current OS"
  (memoize
   (fn []
     (case (os)
       :Ubuntu (if (>= (ubuntu-version) 20.04) "/usr/bin/bash" "/bin/bash")
       :Raspbian_GNU/Linux "/bin/bash"
       :else (throw (ex-info "No matching os found for bash path resolving" {:os (os)}))))))

(defn bash!
  "check that we are running within bash!"
  []
  (script
   ("[" "!" "-n" "\"$BASH\"" "]" "&&" "echo" "Please set default user shell to bash" "&&" "exit" 1)))

(defn validate!
  "validating a bash script"
  [f]
  {:pre (fn? f)}
  (do-script (bash!) (f)))

(defn shell-args
  "Gets a function that produces a set of args for the re-cog shell function:
    * Prepends a check that bash is the default user shell to the script arg
    * Adds a checksum of the script as a second arg
    * The cached? optional argument will store the generated script enhancing performance for repeatedly executed scripts
  "
  [script-fn & {:keys [cached? wait?] :or {cached? false wait? true}}]
  {:pre (fn? script-fn)}
  [(md5 (script-fn)) (validate! script-fn) :cached? cached? :wait wait?])

(defn bind-bash
  "Bind stevedore language to bash"
  []
  (.bindRoot (var pallet.stevedore/*script-language*) :pallet.stevedore.bash/bash))

(def-serial shell
  "Remotely execute a script with a provided sum value (not intended to be used locally or as a resource)"
  [sum script & {:keys [cached? wait?] :or {cached? false wait? true}}]
  (let [f (fs/file (fs/tmpdir) sum)]
    (try
      (when-not (fs/exists? f)
        (spit f script))
      (if wait?
        (sh (bash-path) (.getPath f))
        (try
          (let [process (. (Runtime/getRuntime) exec (into-array String [(bash-path) (.getPath f)]))
                exit (. process exitValue)]
            (if (= exit "process hasn't exited")
              {:out "Process is running" :err "" :exit 0}
              {:out "Process failed to run" :err "" :exit exit}))
          (catch java.io.IOException e
            {:out "Process failed to run" :err (. e getMessage) :exit 1})))
      (finally
        (when-not cached?
          (.delete f))))))
