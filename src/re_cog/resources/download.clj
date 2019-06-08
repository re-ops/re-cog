(ns re-cog.resources.download
  (:require
   [digest :as digest]
   [me.raynes.fs :refer (exists?)]
   [taoensso.timbre :refer (refer-timbre)]
   [re-cog.common :refer (defn)]
   [clojure.java.io :as io]))

(defn download
  "Download file resource, if checksum is provided download will be lazy:
      (download url dest expected :sha256) ; download only if file missing or checksum mismatch
      (download url dest); always download "
  [url dest checksum & {:keys [hash-type] :or {hash-type :sha256}}]
  (let [digest-fns {:sha256 digest/sha-256 :md5 digest/md5}]
    (when (or (not (exists? dest)) (not (= checksum ((digest-fns hash-type) (io/as-file dest)))))
      (with-open [in (io/input-stream url) out (io/output-stream dest)]
        (io/copy in out))
      (assert (= checksum ((digest-fns hash-type) (io/as-file dest)))))))
