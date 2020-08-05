(ns re-cog.resources.random
  "Random password generator"
  (:require
   [re-cog.common.defs :refer (def-serial)]
   [re-cog.common.functions :refer (success)]
   [clojure.edn :as edn]
   [clojure.java.io :refer (file)]))

(def-serial passphrase
  "Create a random passphrase with provided size for key in provided dest file"
  [dest size k]
  (let [special (into #{} " !#$%&'()*+,-./:;<=>?@[\\]^_`{|}~")
        a-z (into #{} (map char (range 97 123)))
        random (java.security.SecureRandom.)]
    (letfn [(generate []
              (apply str
                     (take size (filter (some-fn special a-z)
                                        (map char (iterator-seq (.iterator (.ints random 0 65535))))))))
            (user-file []
                       (let  [file (clojure.java.io/file dest)]
                         (when (.createNewFile file)
                           (java.nio.file.Files/setPosixFilePermissions
                            (.toPath file)
                            #{java.nio.file.attribute.PosixFilePermission/OWNER_READ
                              java.nio.file.attribute.PosixFilePermission/OWNER_WRITE})
                           (spit dest "{}"))))

            (persist [k]
                     (let [data (clojure.edn/read-string (slurp dest))]
                       (when-not (contains? data k)
                         (not (spit dest (with-out-str (pr (assoc data  k (generate)))))))))]
      (user-file)
      (if (persist k)
        (success "new passphrase created")
        (success "passphrase already exists")))))
