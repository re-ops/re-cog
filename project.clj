(defproject re-cog "0.1.0"
  :description "Distributed provisioning Clojure functions to be used with Re-gent"
  :url "https://github.com/re-ops/re-cog"
  :license {:name "Apache License, Version 2.0" :url "http://www.apache.org/licenses/LICENSE-2.0.html"}
  :dependencies [
     [org.clojure/clojure "1.10.0"]
     [org.clojure/core.incubator "0.1.4"]

     ; serialization
     [serializable-fn "1.1.4"]
     [com.taoensso/nippy "2.14.0"]

     ; fs utilities
     [me.raynes/fs "1.4.6"]

     ; logging
     [com.taoensso/timbre "4.10.0"]
     [timbre-ns-pattern-level "0.1.2"]
     [com.fzakaria/slf4j-timbre "0.3.8"]

     ; common utilities and shared functions
     [re-share "0.11.0"]
  ]
)
