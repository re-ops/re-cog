(ns re-cog.common.grant
  "Sudoer related analysis of resource usage"
  (:require
   [clojure.core.match :refer [match]]
   [clojure.walk :as walk]))

(defn binary [b]
  (cond
    (seq? b) [:binary (deref (resolve (second b)))]
    (symbol? b) [:binary (deref (resolve b))]
    :else [:binary b]))

(defn into-spec [cmd]
  (into [(binary (first cmd))]
        (map
         (fn [e]
           (cond
             (seq? e) [:var (second e)]
             (symbol? e) [:var e]
             (and (string? e) (.startsWith e "-"))  [:flag e]
             :else [:arg e]))
         (rest cmd))))

(defn sudo-calls
  "Track sudo usage in serialized functions"
  [s-exp]
  (let [calls (atom [])]
    (walk/postwalk
     (fn [v]
       (match [v]
         [(["sudo" & r] :seq)] (do (swap! calls conj (into-spec r)) v)
         [(['sh "sudo" & r] :seq)] (do (swap! calls conj (into-spec r)) v)
         :else v)) s-exp)
    @calls))
