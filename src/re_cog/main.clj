(ns re-cog.main
  (:gen-class)
  (:require
   [re-cog.common :refer (bind-bash)]
   [re-cog.plan :refer :all]))

(defn -main [& args]
  (bind-bash)
  (let [namespaces (deref (resolve (symbol (first args))))]
    (doseq [n namespaces]
      (require n))
    (doseq [f (execution-plan namespaces)]
      (f))))

(comment
  ((first (-main ["re-cog.plan/lean"]))))
