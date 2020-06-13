(ns re-cog.resources.ufw
  "UFW firewall resource"
  (:require
   [re-cog.common.functions :refer (require-functions)]
   [re-cog.common.constants :refer (require-constants)]
   [re-cog.common.defs :refer (def-serial)]))

(require-functions)
(require-constants)

(def-serial add-rule
  "Add a UFW rule:
   (add-rule 22 :allow 1 :from ip :on \"eth-0\" :proto \"tcp\")
   "
  [port state opts]
  (letfn [(add []
            (let [from (opts :from "any")
                  on (opts :on "any")
                  state' (name state)
                  proto (opts :proto "any")]
              (cond
                (opts :on) (script
                            ("sudo" ~ufw-bin ~state' "in" "on" ~on "to" "any" "port" ~port "proto" ~proto))
                (opts :from) (script
                              ("sudo" ~ufw-bin ~state' "from" ~from "to" "any" "port" ~port "proto" ~proto))
                :else (script
                       ("sudo" ~ufw-bin ~state' "to" "any" "port" ~port "proto" ~proto)))))]
    (assert (#{:allow :deny} state))
    (run- add)))

(def-serial del-rule
  "Delete a UFW rule:
   (del-rule 1)
   "
  [position]
  (letfn [(delete []
            (script
             (pipe "/usr/bin/yes" ("sudo" ~ufw-bin "delete" ~position))))]
    (run- delete)))

(def-serial reset
  "Reset UFW into a clean state
    (reset)
   "
  []
  (letfn [(reset []
            (script
             (pipe "/usr/bin/yes" ("sudo" ~ufw-bin "reset"))))]
    (run- reset)))

(def-serial set-state
  "Add a UFW rule:
   (add-rule 22 :allow 1 :from ip :on \"eth-0\" :proto \"tcp\")
   "
  [state]
  (letfn [(set- [k]
            (fn [] (script
                    ("sudo" ~ufw-bin "--force" ~k))))]
    (assert (#{:enable :disable} state))
    (run- (set- (name state)))))
