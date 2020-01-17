(ns re-cog.main
  (:gen-class)
  (:require
   [re-cog.plan]
   [re-cog.scripts.common :refer (bind-bash)]))

(defn -main [& args]
    (println "Starting to rock!")
    (bind-bash)
    (let [namespaces (deref (resolve (symbol (first args))))]
      (doseq [n namespaces]
        (require n))
      (doseq [f (re-cog.plan/execution-plan namespaces)]
        (f))))

(comment
  ((first (-main ["re-cog.plan/lean"]))))
