(ns user
  (:require
   [clojure.repl :refer :all]
   [re-cog.common :refer :all]
   [clojure.tools.namespace.repl :refer (refresh refresh-all)]))

(defn stop
  "Shuts down and destroys the current development system."
  []
  )

(defn go
  "Initializes the current development system and starts it running."
  []
  (bind-bash))

(defn reset []
  (stop)
  (refresh :after 'user/go))

(defn clrs
  "clean repl"
  []
  (print (str (char 27) "[2J"))
  (print (str (char 27) "[;H")))

(defn require-tests []
  )

(defn run-tests []
  (clojure.test/run-tests))
