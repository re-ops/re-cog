(ns re-cog.resources.file
  "File resources"
  (:require
   [re-cog.common.functions :refer (require-functions)]
   [re-cog.common :refer (def-serial require-constants)]))

(require-functions)

(def-serial directory
  "Directory resource:
    (directory \"/tmp/bla\") ; create directory
    (directory \"/tmp/bla\" :present) ; explicit create
    (directory \"/tmp/bla\" :absent) ; remove directory"
  [path state]
  (println 1))

(def-serial file
  "A file resource"
  [path state]
  (println 1))

(def-serial symlink
  "Symlink resource"
  [path target]
  (letfn [(symlink-target [t]
            (let [f (java.nio.file.Paths/get (java.net.URI. (<< "file://~{t}")))]
              (str (java.nio.file.Files/readSymbolicLink f))))]
    (if (fs/exists? path)
      (let [existing (symlink-target path)]
        (when-not (= target existing)
          (throw
           (ex-info "Symlink path exist but does not point to target"
                    {:existing existing :expected path :target target}))))
      (fs/sym-link path target))))

(def-serial template
  "Template resource"
  [tmpl dest args]
  (println 1))

(def-serial copy
  "Copy a local file:
    (copy src dest)
  "
  [src dest]
  (fs/copy src dest))

(def-serial chown
  "Change file/directory ownership
    (chown \"/home\"/re-ops/.ssh\" \"foo\" \"bar\"); using user/group
    (chown \"/home\"/re-ops/.ssh\" \"foo\" \"bar\" {:recursive true}); chown -R"
  [dest user group & args]
  (println 1))
