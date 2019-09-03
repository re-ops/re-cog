(ns re-cog.recipes.clojure
  "Setting up Clojure tooling"
  (:require
   [re-cog.facts.config :refer (configuration)]
   [re-cog.resources.download :refer (download)]
   [re-cog.resources.archive :refer (unzip)]
   [re-cog.resources.file :refer (symlink directory chmod chown)]
   [re-cog.common.functions :refer (require-functions require-resources)]
   [re-cog.common :refer (require-constants)]
   [re-cog.common.defs :refer (def-inline)]))

(require-functions)
(require-resources)
(require-constants)

(def-inline clj
  "Setting up clj and deps tools"
  []
  (letfn [(install-fn [ins prefix]
            (fn []
              (script (~ins "--prefix" ~prefix))))]
    (let [{:keys [home user]} (configuration)
          ins "linux-install-1.10.1.469.sh"
          url (<< "https://download.clojure.org/install/~{ins}")
          sum "a3c08a18ea09505916685ccfcffc09fe583ed999745a765260ae933e65fc5256"
          prefix (<< "~{home}/.clojure")]

      (download url (<< "/tmp/~{ins}") sum)
      (package "curl" :present)
      (chmod (<< "/tmp/~{ins}") "+x")
      (run (install-fn (<< "/tmp/~{ins}") prefix))
      (directory (<< "~{home}/bin/") :present)
      (symlink (<< "~{home}/bin/clj") (<< "~{prefix}/bin/clj"))
      (chown prefix user user {:recursive true}))))

(def-inline joker
  "Setting up Joker linter"
  []
  (let [{:keys [home]} (configuration)
        version "0.12.0"
        archive (<< "joker-~{version}-linux-amd64.zip")
        url (<< "https://github.com/candid82/joker/releases/download/v~{version}/~{archive}")]
    (download url (<< "/tmp/~{archive}"))
    (directory (<< "~{home}/bin/") :present)
    (unzip (<< "/tmp/~{archive}") (<< "~{home}/bin/"))))
