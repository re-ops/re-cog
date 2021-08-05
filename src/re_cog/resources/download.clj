(ns re-cog.resources.download
  (:require
   [clojure.core.strint :refer (<<)]
   [re-cog.common.functions :refer (file-checksum coherce success failure)]
   [me.raynes.fs :as fs]
   [clojure.java.io :as io]
   [re-cog.common.defs :refer (def-serial)]))

(def-serial checksum
  "File checksum with optional hash-type (:sha256 by default)"
  [f sum & opts]
  (coherce (= sum (file-checksum f opts))))

(def-serial download
  "Download file resource, if checksum is provided download will be lazy:
      (download url dest expected :hash-type :sha256) ; download only if file missing or checksum mismatch
     "
  [url dest sum & opts]
  (if (not (fs/exists? (fs/parent dest)))
    (failure (<< "parent folder missing, cannot download file into ~{dest}"))
    (if (or (not (fs/exists? dest)) (not (= sum (file-checksum dest opts))))
      (do
        (with-open [in (io/input-stream url) out (io/output-stream dest)]
          (io/copy in out))
        (let [found (file-checksum dest opts)]
          (if (= sum found)
            (success "Downloaded file successfuly")
            (failure (<< "Found checksum ~{found} of downloaded file does not match provided ~{sum}")))))
      (success "File already download and checksum matches"))))
