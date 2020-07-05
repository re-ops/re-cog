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

(defn temp-script []
  (do-script
   (vm-fail)
   (script
    (set! R @(pipe ("cat" "/proc/cpuinfo") ("awk" "'/model name/{print $4;exit}'")))
    (case @R
      "'Intel(R)'" ("sensors -A -u")
      "AMD" ("sensors -A -u")
      "ARMv7" (pipe ("cat" "/sys/class/thermal/thermal_zone0/temp") ("/usr/bin/awk" "'{printf(\"cpu\ntemp1:\n  temp1_input: %s\", $1/1000)'"))
      "*" (do (println "'no matching cpu type found'") ("exit" 1))))))
