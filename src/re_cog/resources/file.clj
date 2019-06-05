(ns re-cog.resources.file
  "File resources"
  (:require
   [re-cog.resources.common :refer (defn)]))

(defn directory
  "Directory resource:
    (directory \"/tmp/bla\") ; create directory
    (directory \"/tmp/bla\" :present) ; explicit create
    (directory \"/tmp/bla\" :absent) ; remove directory"
  [path state]
  (println 1))

(defn file
  "A file resource"
  [path state]
  (println 1))

(defn symlink
  "Symlink resource"
  [& args]
  (println 1))

(defn template
  "Template resource"
  [tmpl dest args]
  (println 1))

(defn copy
  "Copy a local file remotly:
    (copy src dest)
  "
  [tmpl dest args]
  1)

(defn chown
  "Change file/directory ownership
    (chown \"/home\"/re-ops/.ssh\" \"foo\" \"bar\"); using user/group
    (chown \"/home\"/re-ops/.ssh\" \"foo\" \"bar\" {:recursive true}); chown -R"
  [dest user group & args]
  (println 1))
