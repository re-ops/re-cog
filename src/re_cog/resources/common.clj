(ns re-cog.resources.common
  "Common resource functions"
  (:require
   [serializable.fn :as s]))

(defn into-spec [m args]
  (if (empty? args)
    m
    (let [a (first args)]
      (cond
        (or (fn? a) (string? a)) (into-spec (clojure.core/update m :args (fn [v] (conj v a))) (rest args))
        (keyword? a) (into-spec (assoc m :state a) (rest args))))))

(defmacro defn
  "Define a serialized function"
  ([name doc args body]
   `(def ^{:doc ~doc} ~name (s/fn ~args ~body)))
  ([name doc args pre-post body]
   `(def ^{:doc ~doc :prepost pre-post} ~name (s/fn ~args ~body))))
