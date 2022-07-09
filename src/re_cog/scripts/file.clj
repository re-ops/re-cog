(ns re-cog.scripts.file
  "FS scripts"
  (:require [pallet.stevedore :refer (script)]))

(defn purge [n dest]
  (let [arg-n (str "+" n)]
    (script
      ;; ls -tp | grep -v '/$' | tail -n +6 | xargs -I {} rm -- {}
     ("cd" ~dest)
     (pipe
      (pipe
       (pipe ("/bin/ls" "-tp") ("/bin/grep" "-v" "'/$'"))
       ("/usr/bin/tail" "-n" ~arg-n))
      ("/usr/bin/xargs" "-I" "{}" "rm" "--" "{}")))))
