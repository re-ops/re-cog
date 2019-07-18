(ns re-cog.resources.file
  "File resources"
  (:require
   [re-cog.resources.exec :refer (run)]
   [re-cog.common.functions :refer (require-functions)]
   [re-cog.common.defs :refer (def-serial)]
   [re-cog.common :refer (require-constants)]))

(require-functions)

(def-serial directory
  "Directory resource:
    (directory \"/tmp/bla\" :present) ; create
    (directory \"/tmp/bla\" :absent) ; remove"
  [dest state]
  (let [states {:present fs/mkdir :absent fs/delete-dir}]
    ((states state) dest)))

(def-serial file
  "A file resource"
  [path state]
  (let [states {:present fs/touch :absent fs/delete}]
    ((states state) path)))

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

(def-serial chmod
  "Change file/directory mode resource:
    (chmod \"/home\"/re-ops/.ssh\" \"0777\")
    (chmod \"/home\"/re-ops/.ssh\" \"0777\" :recursive true)
  "
  [dest mode & options]
  (letfn [(chmod-script []
            (if (contains? options :recursive)
              (script ("/bin/chmod" ~mode ~dest))
              (script ("/bin/chmod" ~mode ~dest "-R"))))]
    (run chmod-script)))

(def-serial chown
  "Change file/directory owner using uid & gid resource:
      (chown \"/home\"/re-ops/.ssh\" \"foo\" \"bar\"); using user/group
      (chown \"/home\"/re-ops/.ssh\" \"foo\" \"bar\" {:recursive true}); chown -R    
     "
  [dest user group opts]
  (letfn [(chown-script []
            (let [u-g (<< "~{user}:~{group}")]
              (if (opts :recursive)
                (script ("/bin/chown" ~u-g ~dest))
                (script ("/bin/chown" ~u-g ~dest "-R")))))]
    (run chown-script)))

