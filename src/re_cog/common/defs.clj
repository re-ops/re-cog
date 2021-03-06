(ns re-cog.common.defs
  "Def macros"
  (:require
   [serializable.fn :as s]
   [re-share.core :refer [measure gen-uuid]]
   [clojure.spec.alpha :as sp]
   [clojure.repl :refer (source-fn)]
   [re-cog.meta :refer (resource-functions)]
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
     `(def ~name (s/fn ~args ~body')))))

(defn source-of
  "Get the source of a function (works only from the repl)"
  [f]
  (read-string (clojure.repl/source-fn f)))

(defn inlined
  "Capture function source for inlining"
  [f profile]
  (let [[_ name _ args body] (source-of (symbol f))
        fqn (str f)
        m (gensym "m")
        args-only (gensym "args-only")]
    (list name args
          (list 'let [m (list 're-share.core/measure (list 'fn [] body))
                      args-only (filterv (fn [a] (not (= "&" (str a)))) args)]
                (list 'swap! profile 'conj
                      (list 'merge
                            (list 'dissoc m :result)
                            (list m :result)
                            {:type fqn
                             :args (list 'filterv (list 'comp 'not 'fn?) args-only)
                             :uuid (list 're-share.core/gen-uuid)}))
                (m :result)))))

(defn distinct-by [f coll]
  (let [groups (group-by f coll)]
    (mapv #(first (groups %)) (distinct (map f coll)))))

(defn inlined-functions [body profile]
  "Doing a postwalk on the body s-exp inlining the first level of serializable functions"
  (let [fs (atom [])]
    (postwalk
     (fn [exp]
       (when (symbol? exp)
         (when-let [f ((resource-functions) exp)]
           (swap! fs conj (inlined f profile))))) body)
    (distinct-by first @fs)))

(defmacro def-inline
  "Construct a serialized function where:
    * Each serialized function within its body is inlined using a letfn form.
    * The result of the function is a map containing the output of each nested call and its measured runtime.
    * Using meta data :depends we can specify dependencies between recipe functions that will be accounted for in re-cog.plan/execution-plan."
  [& args]
  (let [{:keys [name args meta body]} (parse-args args)
        profile (gensym 'profile)
        letfn-vec (inlined-functions body profile)
        body' (do-body body)
        sum (re-share.core/md5 (str body'))
        used-functions (mapv (comp keyword first) letfn-vec)
        meta+ (merge meta {:sum sum :resources used-functions})]
    `(do
       (def ~name
         (s/fn ~args (let [~profile (atom #{})]
                       (letfn ~letfn-vec
                         (let [result# ~body']
                           (if-let [e# (first (filter (fn [m#] (not (= (m# :exit) 0))) (deref ~profile)))]
                             e#
                             (merge result# {:resources (deref ~profile)})))))))
       (alter-meta! (var ~name) #(merge % ~meta+)))))

(defn require-defs
  "Require common constant values"
  []
  (require
   '[re-cog.common.defs :refer [def-inline def-serial]]))
