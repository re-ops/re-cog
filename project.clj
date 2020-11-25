(defproject re-cog "0.5.16"
  :description "Re-cog is a set of serializable functions including provisioning resources, system facts and commonly used remotely executable functions"
  :url "https://github.com/re-ops/re-cog"
  :license {:name "Apache License, Version 2.0" :url "http://www.apache.org/licenses/LICENSE-2.0.html"}
  :dependencies [
     [org.clojure/clojure "1.10.1"]
     [org.clojure/core.incubator "0.1.4"]

     ; serialization
     [serializable-fn "1.1.4"]
     [com.taoensso/nippy "2.14.0"]
     [cheshire "5.8.1"]

     ; fs utilities
     [me.raynes/fs "1.4.6"]

     ; checksumming
     [digest "1.4.9"]

     ; templating
     [cljstache "2.0.6"]

     ; logging
     [com.taoensso/timbre "4.10.0"]
     [timbre-ns-pattern-level "0.1.2"]
     [com.fzakaria/slf4j-timbre "0.3.8"]

     ; common utilities and shared functions
     [re-share "0.16.6"]
     [re-scan "0.2.1"]

     ; clojure to bash
     [com.palletops/stevedore "0.8.0-beta.7"]

     ; repl
     [org.clojure/tools.namespace "0.3.1"]

     ; planning
     [aysylu/loom "1.0.2" :exclusions [org.clojure/clojurescript]]

     ; CLI
     [cli-matic "0.3.11"]
     [progrock "0.1.2"]

     ; in memory datalog
     [datascript "0.18.8"]
     [camel-snake-kebab "0.4.1"]

     ; configuration
     [aero "1.1.6"]
  ]

  :plugins [
      [lein-tag "0.1.0"]
      [lein-ancient "0.6.15" :exclusions [org.clojure/clojure]]
      [lein-set-version "0.3.0"]
      [lein-cljfmt "0.5.6"]]

   :aliases {
     "travis" [
       "do" "clean," "compile," "cljfmt" "check"
     ]
     "docs" [
         "with-profile" "codox" "do" "codox"
     ]
   }

  :repl-options {
    :init-ns user
    :prompt (fn [ns] (str "\u001B[35m[\u001B[34m" "re-cog" "\u001B[35m]\u001B[33mλ:\u001B[m " ))
    :welcome (println "Welcome to re-cog!" )
  }

  :source-paths  ["src" "dev"]

  :profiles {
     :dev {
       :source-paths  ["dev"]
       :set-version {
         :updates [
            {:path "README.md" :search-regex #"\d+\.\d+\.\d+"}
          ]}
     }

     :codox {
        :dependencies [[org.clojure/tools.reader "1.1.0"]
                       [codox-theme-rdash "0.1.2"]]
              :plugins [[lein-codox "0.10.7"]]
              :codox {:project {:name "re-core"}
                      :themes [:rdash]
                      :source-paths ["src"]
                      :source-uri "https://github.com/re-ops/re-core/blob/master/{filepath}#L{line}"
              }
    }
  }


)
