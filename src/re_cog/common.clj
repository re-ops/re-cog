(ns re-cog.common
  "Common resource functions"
  (:require
   [re-share.core :refer [measure]]
   [pallet.stevedore]
   [clojure.repl]
   [serializable.fn :as s]))

(defn into-spec [m args]
  (if (empty? args)
    m
    (let [a (first args)]
      (cond
        (or (fn? a) (string? a)) (into-spec (clojure.core/update m :args (fn [v] (conj v a))) (rest args))
        (keyword? a) (into-spec (assoc m :state a) (rest args))))))

(defmacro def-serial
  "Define a serializable function"
  ([name doc args body]
   `(def ^{:doc ~doc} ~name (s/fn ~args ~body)))
  ([name doc args pre-post body]
   `(def ^{:doc ~doc :prepost pre-post} ~name (s/fn ~args ~body))))

(defn source-of
  "Get the source of a function (works only from the repl)"
  [f]
  (read-string (clojure.repl/source-fn (first f))))

(defn source-list
  "Deconstruct function source into its componenets"
  [f]
  (let [[_ name _ args body] (source-of f)]
    (list name args body)))

(defn letfn-
  "Letfn for for inlined functions"
  [body]
  (into [] (map source-list body)))

(defn name-of
  "Get the name of f"
  [f]
  (second (source-of f)))

(defn nested-body [body result]
  (reduce
   (fn [acc [b name]]
     (list 'let [(symbol (str name "-p")) (list 're-share.core/measure (list 'fn [] b))] acc)) result
   (map (juxt identity name-of) (reverse body))))

(defn deconstruct-let [[first-form & _ :as body]]
  (if (= 'let (first first-form))
    {:let-vec (first (rest first-form)) :body (rest (rest first-form))}
    {:let-vec [] :body body}))

(defmacro def-inline
  "Construct a serialized function (composed from a sequence of serialized functions) where:
    * Each function within its body is inlined using a letfn form.
    * The result of the function is the output of each nested call and its measured runtime.
  "
  [name doc args & outer-form]
  (let [{:keys [body let-vec]} (deconstruct-let outer-form)
        letfn-vec (letfn- body)
        names (map name-of body)
        profiles (map (fn [name] (symbol (str name "-p"))) names)
        result (zipmap (map keyword names) profiles)
        nested (nested-body body result)
        new-body (list 'let let-vec nested)]
    `(def-serial ~name ~doc ~args
       (letfn ~letfn-vec
         ~new-body))))

(defn bind-bash
  "Bind stevedore language to bash"
  []
  (.bindRoot (var pallet.stevedore/*script-language*) :pallet.stevedore.bash/bash))

; Constants

(def apt-bin "/usr/bin/apt")

(def dpkg-bin "/usr/bin/dpkg")

(defn require-constants
  "Require common constant values"
  []
  (require
   '[re-cog.common :refer [apt-bin dpkg-bin]]))
