(ns re-cog.main
  (:require
   [re-cog.plan]
   [re-cog.scripts.common :refer (bind-bash)]))

#_(defn -main [& args]
    (bind-bash)
    (let [namespaces (deref (resolve (symbol (first args))))]
      (doseq [n namespaces]
        (require n))
      (doseq [f (re-cog.plan/execution-plan namespaces)]
        (f))))

(comment
  ((first (-main ["re-cog.plan/lean"]))))
