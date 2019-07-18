(ns re-cog.common.defs
  "Def macros"
  (:require
   [serializable.fn :as s]
   [re-share.core :refer [measure]]
   [clojure.spec.alpha :as sp]
   [clojure.repl :refer (source-fn)]
   [re-cog.meta :refer (functions)]
   [clojure.walk :refer (postwalk)]))

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

(defn do-body
  "Surround multi body s-exp with do"
  [body]
  (if-not (= 1 (count body))
    (cons 'do (apply list body))
    (first body)))

(defmacro def-serial
  "Define a serializable function"
  ([& in-args]
   (let [{:keys [name args meta body]} (parse-args in-args)
         body' (do-body body)]
     `(def ^{:serializable true} ~name (s/fn ~args ~body')))))

(defn source-of
  "Get the source of a function (works only from the repl)"
  [f]
  (read-string (clojure.repl/source-fn f)))

(defn inlined
  "Capture function source for inlining"
  [f profile]
  (let [[_ name _ args body] (source-of f)
        r (gensym 'r)
        t (gensym 't)]
    (list name args
          (list 'let [[r t] (list 're-share.core/measure (list 'fn [] body))]
                (list 'swap! profile 'assoc (keyword name) [r t])
                r))))

(def core-functions (ns-publics 'clojure.core))

(defn inlined-functions [body profile]
  "Doing a postwalk on the body s-exp inlining the first level of serializable functions"
  (let [fs (atom [])]
    (postwalk
     (fn [exp]
       (when (and (symbol? exp) ((functions) exp))
         (swap! fs conj (inlined exp profile)))) body)
    @fs))

(defmacro def-inline
  "Construct a serialized function (composed from a sequence of serialized functions) where:
    * Each function within its body is inlined using a letfn form.
    * The result of the function is the output of each nested call and its measured runtime.
  "
  [& args]
  (let [{:keys [name args meta body]} (parse-args args)
        profile (gensym 'profile)
        letfn-vec (inlined-functions body profile)
        body' (do-body body)]
    `(def ~name
       (s/fn ~args
         (let [~profile (atom {})]
           (letfn ~letfn-vec
             (let [result# ~body']
               (merge result# {:profile (deref ~profile)}))))))))

