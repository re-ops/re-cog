(ns re-cog.scripts.rpi
  (:require
   [pallet.stevedore :refer (script)]
   [clojure.core.strint :refer (<<)]))

(defn set-screen
  "Turn screen off/on on an RPI"
  [state]
  (let [code ({:on 0 :off 1} state)]
    (script
     (pipe ("echo" ~code) ("sudo" "/usr/bin/tee" "/sys/class/backlight/rpi_backlight/bl_power")))))
