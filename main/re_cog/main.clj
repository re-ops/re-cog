(ns re-cog.main
  (:gen-class)
  (:require
   re-cog.plan
   re-cog.facts.config
   [re-share.log :as log]
   [taoensso.timbre  :as timbre :refer (set-level! refer-timbre)]
   [cli-matic.core :refer [run-cmd]]
   [progrock.core :as pr]
   [re-cog.facts.datalog :refer (populate)]
   [re-cog.scripts.common :refer (bind-bash)]))

(refer-timbre)

(defn setup-logging
  "Sets up logging configuration:
    - stale logs removale interval
    - steam collect logs
    - log level
  "
  [& {:keys [interval level] :or {interval 10 level :info}}]
  (log/setup "re-cog" ["oshi.*"] ["re-cog.plan"])
  (set-level! level))

(defn resolve- [s]
  (deref (resolve (symbol s))))

(defn provision [{:keys [plan config level]}]
  (setup-logging :level level)
  (populate)
  (let [namespaces  (resolve- plan)]
      (doseq [n namespaces]
        (require n))
      (let [fs (re-cog.plan/execution-plan namespaces)
            bar (pr/progress-bar (count fs))]
        (doseq [[i f] (map-indexed vector fs)]
          (pr/print (pr/tick bar i))
          (info "Running" f)
          (f))
        (pr/print (pr/done bar)))))


(def CONFIGURATION {
    :app {
      :command     "re-cog"
      :description "Re-cog provisioning cli"
      :version     "0.3.2"
    }
    :commands [
      {:command "provision" :short "p!"
       :description "Run a provisioning plan"
       :opts [{:option "config" :short "c" :env "CC" :as "configuration file" :type :ednfile :default "/tmp/resources/config.edn"}
              {:option "plan" :short "p" :env "PP" :as "plan" :type :string :default :present}
              {:option "level" :short "l" :env "LL" :as "log level" :type :keyword :default :info}]
       :runs  provision}
    ]
  })

(defn -main [& args]
    (bind-bash)
    (run-cmd args CONFIGURATION))

(comment
  ((first (-main ["re-cog.plan/lean"]))))
