(ns re-cog.scripts.common
  (:require
   [re-share.core :refer (md5)]
   [pallet.stevedore :refer (script do-script)]))

(defn bash!
  "check that we are running within bash!"
  []
  (script
   ("[" "!" "-n" "\"$BASH\"" "]" "&&" "echo" "Please set default user shell to bash" "&&" "exit" 1)))

(defn validate!
  "validating a bash script"
  [f]
  (do-script (bash!) (f)))

(defn shell-args
  "Gets a function that produces a set of args for the re-cog shell function:
    * Prepends a check that bash is the default user shell to the script arg
    * Adds a checksum of the script as a second arg
  "
  [script-fn]
  [(md5 (script-fn)) (validate! script-fn)])
