(ns re-cog.scripts.desktop
  "Desktop remote control scripts"
  (:require
   [clojure.string :refer (join capitalize)]
   [pallet.stevedore :refer (script)]
   [clojure.core.strint :refer (<<)]))

(defn killall
  "killall processes by name"
  [p]
  (fn []
    (script
     ("/usr/bin/killall" ~p))))

(defn xmonad
  "Launch xmonad"
  []
  (fn []
    (script
     ("/usr/bin/pgrep" "xmonad")
     (when (not (= $? 0))
       ("export" (set! DISPLAY ":0"))
       ("/usr/bin/xmonad" "--replace")))))

(defn librewriter
  "Launch a docment in libreoffice-writer in view only mode"
  [doc]
  (fn []
    (let [cmd (<< "\"/usr/bin/libreoffice --view '~{doc}' &\"")]
      (script
       ("export" (set! DISPLAY ":0"))
       ("nohup" "sh" "-c" ~cmd)))))

(defn fullscreen-chrome
  "Launch chrome in full screen"
  [url]
  (fn []
    (script
     (if (file-exists? "/usr/bin/google-chrome")
       (set! browser "/usr/bin/google-chrome")
       (if (file-exists? "/snap/bin/chromium")
         (set! browser "/snap/bin/chromium")
         (do
           (println "chrome not found!")
           ("exit" 1))))
     ("export" (set! DISPLAY ":0"))
     ("nohup" "sh" "-c" "\"$browser" "--start-fullscreen" "--new-window" "'" ~url "'" "&\"")
     ("exit" 0))))

(defn xdot-type
  "xdot type string seqeuence"
  [s]
  (fn []
    (script
     (if (file-exists? "/usr/bin/xdotool")
       (do
         ("export" (set! DISPLAY ":0"))
         ("/usr/bin/xdotool" "type" ~s))
       (do
         (println "xdot not found!")
         ("exit" 1))))))

(defn xdot-key
  "Click a keyboard key"
  [ks]
  (fn []
    (let [combination (join "+" (map (comp capitalize name) ks))]
      (script
       (if (file-exists? "/usr/bin/xdotool")
         (do
           ("export" (set! DISPLAY ":0"))
           ("/usr/bin/xdotool" "key" ~combination))
         (do
           (println "xdot not found!")
           ("exit" 1)))))))

(defn shift-screen
  "Move current window to screen n"
  [n]
  (xdot-key :alt :shift (str n)))

(defn goto-screen
  "Move to screen n"
  [n]
  (xdot-key :alt (str n)))

(defn clear-cache
  "Clearing chrome cache"
  []
  (fn []
    (script ("rm" "-rf" "/home/ronen/snap/chromium/common/.cache"))))
