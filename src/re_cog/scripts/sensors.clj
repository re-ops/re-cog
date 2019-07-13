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
   (script (set! R @(pipe ("cat" "/proc/cpuinfo") ("awk" "'/model name/{print $4;exit}'")))
           (case @R
             "'Intel(R)'" (pipe (pipe ("sensors -A") ("grep" "coretemp" "-A" "1000")) ("awk" "'{$1=$1};1'"))
             "AMD" (pipe (pipe ("sensors -A") ("grep" "temp")) ("awk" "'{$1=$1};1'"))
             "ARMv7" (do (let t ("cat" "/sys/class/thermal/thermal_zone0/temp")) (/ t 1000))
             "*" (do (println "'no matching cpu type found'") ("exit" 1))))))
