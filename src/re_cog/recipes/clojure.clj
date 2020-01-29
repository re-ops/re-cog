(ns re-cog.recipes.clojure
  "Setting up Clojure tooling"
  (:require
   [re-cog.resources.exec :refer [run]]
   [re-cog.common.recipe :refer (require-recipe)]
   [re-cog.facts.config :refer (configuration)]
   [re-cog.resources.download :refer (download)]
   [re-cog.resources.archive :refer (unzip)]
   [re-cog.resources.file :refer (symlink directory chmod chown)]))

(require-recipe)

(def-inline clj
  "Setting up clj and deps tools"
  []
  (letfn [(install-fn [ins prefix]
            (fn []
              (script (~ins "--prefix" ~prefix))))]
    (let [{:keys [home user]} (configuration)
          ins "linux-install-1.10.1.469.sh"
          url (<< "https://download.clojure.org/install/~{ins}")
          sum "265e46d492b682cecc346ef018076a3181203dfe5e3dbe455b0f7feab51df70f"
          prefix (<< "~{home}/.clojure")]
      (download url (<< "/tmp/~{ins}") sum)
      (package "curl" :present)
      (package "rlwrap" :present)
      (chmod (<< "/tmp/~{ins}") "+x")
      (run (install-fn (<< "/tmp/~{ins}") prefix))
      (directory (<< "~{home}/bin/") :present)
      (symlink (<< "~{home}/bin/clj") (<< "~{prefix}/bin/clj"))
      (symlink (<< "~{home}/bin/clojure") (<< "~{prefix}/bin/clojure"))
      (chown prefix user user {:recursive true}))))

(def-inline joker
  "Setting up Joker linter"
  []
  (let [{:keys [home]} (configuration)
        version "0.12.7"
        archive (<< "joker-~{version}-linux-amd64.zip")
        url (<< "https://github.com/candid82/joker/releases/download/v~{version}/~{archive}")
        sum "25ba334d68044971e556e9aa0ce6c1994610a464c399adf0e0357fd2e23b6c36"]
    (download url (<< "/tmp/~{archive}") sum)
    (directory (<< "~{home}/bin/") :present)
    (unzip (<< "/tmp/~{archive}") (<< "~{home}/bin/"))))

(def-inline profile
  "lein profile"
  []
  (let [{:keys [home]} (configuration)
        base "https://gist.githubusercontent.com/narkisr"
        id "f260d4024ece207616c0bd6307a295c3"
        version  "1f65c2c5f981b01f2ea3b6462d924a691f145253"
        sum "8bdbec70922349b1f7f65816bf3231588de45b145b3bd792c5a6a8924760e09e"
        dest (<< "~{home}/.lein")]
    (directory dest :present)
    (download (<< "~{base}/~{id}/raw/~{version}/profiles.clj") (<< "~{dest}/profiles.clj") sum)))
