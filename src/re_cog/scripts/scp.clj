(ns re-cog.scripts.scp
  "remote scp commands"
  (:require [pallet.stevedore :refer (script)]))

(defn scp-from [user host src target recursive?]
  (fn []
    (let [in (str user "@" host ":" src)
          args (if recursive? "-r" "")]
      (script
       ("/usr/bin/scp" ~args ~in ~target)))))
