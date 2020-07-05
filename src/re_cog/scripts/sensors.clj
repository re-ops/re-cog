(ns re-cog.scripts.sensors
  (:require
   [pallet.stevedore :refer (script do-script)]))

(defn vm-fail []
  (script
   (pipe ("cat" "/proc/cpuinfo") ("grep" "hypervisor"))
   (if (= "$?" 0)
     (do
       (println "'cannot measure temp in a VM'")
       ("exit" 1)))))

(defn temp-script
  "Read system devices temperatures supporting Intel AMD and ARMv7.
   * ARMv7 output matches sensors output format in order to maintain compatible output for upstream processing."
  []
  (do-script
   (vm-fail)
   (script
    (set! R @(pipe ("cat" "/proc/cpuinfo") ("awk" "'/model name/{print $4;exit}'")))
    (case @R
      "'Intel(R)'" ("sensors -A -u")
      "AMD" ("sensors -A -u")
      "ARMv7" (pipe
               (pipe
                ("cat" "/sys/class/thermal/thermal_zone0/temp") ("/usr/bin/awk" "'{print($1/1000)}'"))
               ("/usr/bin/xargs" "/usr/bin/printf" "\"armv7\ncpu:\n temp1_input: %s\ntemp1_max: 85.0\n temp1_crit: 90.0\""))
      "*" (do (println "'no matching cpu type found'") ("exit" 1))))))
