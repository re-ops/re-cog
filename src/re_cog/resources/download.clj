(ns re-cog.resources.download
  (:require
   [re-cog.common.functions :refer (require-functions)]
   [re-cog.common.defs :refer (def-serial)]))

(require-functions)

(def-serial checksum
  "File checksum with optional hash-type (:sha256 by default)"
  [f sum & opts]
  (coherce (= sum (file-checksum f opts))))

(def-serial download
  "Download file resource, if checksum is provided download will be lazy:
      (download url dest expected :sha256) ; download only if file missing or checksum mismatch
      (download url dest); always download "
  [url dest sum & opts]
  (if (or (not (exists? dest)) (not (= sum (file-checksum dest opts))))
    (do
      (with-open [in (io/input-stream url) out (io/output-stream dest)]
        (io/copy in out))
      (coherce (= sum (file-checksum dest opts))))
    (coherce true "" "File already download and checksum matches")))
