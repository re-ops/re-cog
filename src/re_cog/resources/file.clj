(ns re-cog.resources.file
  "File resources"
  (:require
   [re-cog.common :refer (require-functions def-serial require-constants)]))

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
  [& args]
  (println 1))

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
