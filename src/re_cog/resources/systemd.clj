(ns re-cog.resources.systemd
  "Systemd related resources"
  (:require
   [camel-snake-kebab.core :as csk]
   [re-cog.common.defs :refer (def-serial)]
   [clojure.string :refer (upper-case capitalize)]
   [re-cog.common.functions :refer (require-functions)]
   [re-cog.common.constants :refer (require-constants)]))

(require-functions)
(require-constants)

(def-serial set-service
  "Set up a service:
     ; Running a user service in tmux
     (set-service \"foo\" \"run foo\" \"/bin/tmx start --p foo\" )
     ; Providing stop
     (set-service \"foo\" \"run foo\" \"/bin/tmx start --p foo\" {:stop \"/usr/bin/tmux kill-session -t foo \"})
     ; Environment variables
     (set-service \"foo\" \"run foo\" \"/bin/tmx start --p foo\" {:environment {:dispaly :0}})
     ; Wanted by
     (set-service \"foo\" \"run foo\" \"/bin/tmx start --p foo\" {:wanted-by \"default.target\"})
     ; Restart 
     (set-service \"foo\" \"run foo\" \"/bin/tmx start --p foo\" {:restart always :restart-sec 60})
     ; Hardening options
     (set-service \"foo\" \"run foo\" \"/bin/tmx start --p foo\" {:hardening {:private-devices true}})
     ; A user service
     (set-service \"foo\" \"run foo\" \"/bin/tmx start --p foo\" {:user \"re-ops\"})
     ; Environment file
     (set-service \"foo\" \"run foo\" \"/bin/tmx start --p foo\" {:enviroment-file \"re-ops\"})
  "
  [service-name description start {:keys [user environment hardening] :as opts}]
  (let [dest (if user (<< "/home/~{user}/.config/systemd/user/") "/etc/systemd/system/")
        source (slurp (io/resource "main/resources/service.mustache"))
        nested (into {} (map (fn [[k v]] [k {:value v}]) (dissoc opts :environment :hardening)))
        environment (mapv (fn [[k v]] {:name (upper-case (name k)) :value v}) environment)
        hardening (mapv (fn [[k v]] {:name (csk/->PascalCase (name k)) :value v}) hardening)
        args (merge {:start start :description description :environment environment :hardening hardening} nested)
        service (<< "~{service-name}.service")]
    (letfn [(linger []
              (script ("/usr/bin/loginctl" "enable-linger" ~user)))
            (enable []
                    (if user
                      (script (~systemctl-bin "--user" "enable" ~service))
                      (script ("sudo" ~systemctl-bin "enable" ~service))))]
      (if user
        (fs/mkdirs dest)
        (run- linger))
      (spit (<< "~{dest}/~{service}") (render source args))
      (run- enable)
      (success (<< "user service created")))))
