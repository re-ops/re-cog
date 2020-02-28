(ns re-cog.recipes.graal
  "Graal setup"
  (:require
   [re-cog.resources.exec :refer [run]]
   [re-cog.common.recipe :refer (require-recipe)]
   [re-cog.facts.config :refer (configuration)]
   [re-cog.resources.download :refer (download)]
   [re-cog.resources.file :refer (symlink directory)]
   [re-cog.resources.archive :refer (untar)]))

(require-recipe)

(def-inline {:depends #'re-cog.recipes.access/permissions} install
  "Setting up Graal"
  []
  (letfn [(gu [bin pkg]
            (fn []
              (script (~bin "install" ~pkg))))]
    (let [{:keys [home]} (configuration)
          version "19.2.0"
          release (<< "graalvm-ce-linux-amd64-~{version}")
          dest (<< "graalvm-ce-~{version}")
          tmp (<< "/tmp/~{release}.tar.gz")
          expected "9d8a82788c3aaede4a05366f79f8b0b328957d0bb7479c986f6f1354b1c7c4ea"
          url (<< "https://github.com/oracle/graal/releases/download/vm-~{version}/~{release}.tar.gz")]
      (download url tmp expected)
      (untar tmp "/opt/")
      (directory (<< "~{home}/bin/") :present)
      (symlink (<< "~{home}/bin/gu") (<< "/opt/~{dest}/lib/installer/bin/gu"))
      (run (gu (<< "/opt/~{dest}/lib/installer/bin/gu") "native-image")))))
