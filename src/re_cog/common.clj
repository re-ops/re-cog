(ns re-cog.common
  "Common resource functions"
  (:require
   [clojure.spec.alpha :as sp]
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

(sp/def ::basic-def
  (sp/cat :name symbol? :doc string? :args vector? :body (sp/* seq?)))

(sp/def ::meta-def
  (sp/cat :meta map? :name symbol? :doc string? :args vector? :body (sp/* seq?)))

(sp/def ::post-def
  (sp/cat :name symbol? :doc string? :args vector? :post map? :body (sp/* seq?)))

(defn parse-args [args]
  (if-let [verified (first (filter (fn [spec] (sp/valid? spec args)) [::basic-def ::meta-def ::post-def]))]
    (let [{:keys [doc post meta body] :as m} (sp/conform verified args)]
      (merge (select-keys m [:name :args :body])
             {:meta (merge (or meta {}) {:doc (or doc "") :prepost (or post {})})}))
    (throw (ex-info "failed to parse in args" {:args args}))))

(defmacro def-serial
  "Define a serializable function"
  ([& in-args]
   (let [{:keys [name args meta body]} (parse-args in-args)
         single-body (if-not (= 1 (count body)) (cons 'do (apply list body)) (first body))]
     `(def ~name (with-meta (s/fn ~args ~single-body) ~meta)))))

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

(defn deconstruct-let
  "Grab let expression if it exists"
  [[first-form & _ :as body]]
  (if (= 'let (first first-form))
    {:let-vec (first (rest first-form)) :body (rest (rest first-form))}
    {:let-vec [] :body body}))

(defmacro def-inline
  "Construct a serialized function (composed from a sequence of serialized functions) where:
    * Each function within its body is inlined using a letfn form.
    * The result of the function is the output of each nested call and its measured runtime.
  "
  [& args]
  (let [{:keys [name args meta body]} (parse-args args)
        {:keys [body-next let-vec]} (deconstruct-let body)
        letfn-vec (letfn- body-next)
        names (map name-of body-next)
        profiles (map (fn [name] (symbol (str name "-p"))) names)
        result (zipmap (map keyword names) profiles)
        nested (nested-body body-next result)
        final-body (list 'let let-vec nested)]
    `(def ~name
       (with-meta
         (s/fn ~args (letfn ~letfn-vec ~final-body)) ~meta))))

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
