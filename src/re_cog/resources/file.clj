(ns re-cog.resources.file
  "File resources"
  (:require
   [clojure.core.strint :refer (<<)]
   [re-cog.common.resources :refer (run-)]
   [re-cog.common.functions :refer (require-functions)]
   [re-cog.common.defs :refer (def-serial)]))

(require-functions)

(def-serial directory
  "Directory resource:
    (directory \"/tmp/bla\" :present) ; create
    (directory \"/tmp/bla\" :absent) ; remove"
  [dest state]
  (letfn [(lazy-mkdir []
            (if (fs/exists? dest) true (fs/mkdir dest)))
          (lazy-rm []
                   (if (fs/exists? dest) (fs/delete-dir dest) true))]
    (let [states {:present lazy-mkdir :absent lazy-rm}]
      (coherce ((states state))))))

(def-serial file
  "A file resource"
  [path state]
  (letfn [(touch [f]
            (if (fs/exists? f) true (fs/touch f)))
          (delete [f]
                  (if (fs/exists? f) (fs/delete f) true))]
    (let [states {:present touch :absent delete}]
      (coherce ((states state) path)))))

(def-serial symlink
  "Symlink resource"
  [path target]
  (letfn [(symlink-target [t]
            (let [f (java.nio.file.Paths/get (java.net.URI. (<< "file://~{t}")))]
              (str (java.nio.file.Files/readSymbolicLink f))))]
    (if (fs/exists? path)
      (let [existing (symlink-target path)]
        (if-not (= target existing)
          (failure (<< "~{path} alreay points to ~{existing} and not to ~{target}"))
          (success "symlink exists")))
      (let [actual (.getPath (fs/sym-link path target))]
        (if (= path actual)
          (success (<< "symlink from ~{path} to ~{target} created"))
          (failure (<< "failed to create symlink ~{actual} is not ~{path}")))))))

(def-serial template
  "Template resource"
  [tmpl dest args]
  (let [source (slurp tmpl)
        out (render source args)]
    (spit dest out)
    (success (<< "created file from template under ~{dest}"))))

(def-serial copy
  "Copy a local file:
    (copy src dest)
  "
  [src dest]
  (try
    (coherce (= (fs/copy src dest) dest))
    (catch Exception e
      (failure (.getMessage e)))))

(def-serial rename
  "Move a local file:
    (rename \"/tmp/foo\" \"bla\")
  "
  [src dest]
  (try
    (coherce (fs/rename src dest))
    (catch Exception e
      (failure (.getMessage e)))))

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
    (run- chmod-script)))

(def-serial chown
  "Change file/directory owner using uid & gid resource:
      (chown \"/home\"/re-ops/.ssh\" \"foo\" \"bar\"); using user/group
      (chown \"/home\"/re-ops/.ssh\" \"foo\" \"bar\" {:recursive true}); chown -R"
  [dest user group opts]
  (letfn [(chown-script []
            (let [u-g (<< "~{user}:~{group}")]
              (if (opts :recursive)
                (script ("/bin/chown" ~u-g ~dest))
                (script ("/bin/chown" ~u-g ~dest "-R")))))]
    (run- chown-script)))

(def-serial line
  "File line resource either append or remove lines:

    (line \"/tmp/foo\" \"bar\" :present); append line
    (line \"/tmp/foo\" (line-eq \"bar\") :absent); remove lines equal to bar from the file
    (line \"/tmp/foo\" (fn [curr] (> 5 (.length curr))) :absent); remove lines using a function
    (line \"/tmp/foo\" \"bar\" :replace :with \"foo\"); remove lines using a function
  "
  [file v state & {:keys [with]}]
  (letfn [(line-eq [line] (fn [curr] (not (= curr line))))
          (add-line [line]
                    (let [contents (slurp file)]
                      (if (clojure.string/includes? contents line)
                        (success "file already contains line")
                        (do
                          (spit file (str line "\n") :append true)
                          (success "line added to file")))))
          (replace-line [pred]
                        (let [f (if (string? pred) (comp not (line-eq pred)) pred)
                              contents (slurp file)
                              replace-with (fn [line] (if (f line) with line))
                              filtered (map replace-with
                                            (clojure.string/split-lines contents))
                              output (clojure.string/join "\n" filtered)]
                          (if-not (= contents output)
                            (do
                              (spit file output)
                              (success "line replaced in file"))
                            (success "line not present in file"))))
          (rm-line [pred]
                   (let [f (if (string? pred) (line-eq pred) pred)
                         contents (slurp file)
                         filtered (filter f (clojure.string/split-lines contents))
                         output (clojure.string/join "\n" filtered)]
                     (if-not (= contents output)
                       (do
                         (spit file output)
                         (success "line removed from file"))
                       (success "line not present in file"))))]
    (let [fns {:present add-line :absent rm-line :replace replace-line}]
      ((fns state) v))))

(def-serial line-set
  "Set existing line value"
  [dest k v sep]
  (letfn [(set-key [k v sep]
            (fn [line]
              (let [[f & _] (clojure.string/split line (re-pattern sep))]
                (if (= f k)
                  (str k sep v)
                  line))))]
    (if-not (fs/exists? dest)
      (error (<< "~{dest} not found"))
      (let [lines (slurp dest)
            edited (map (set-key k v sep) (clojure.string/split-lines lines))]
        (spit dest (clojure.string/join "\n" edited))
        (success "value in file line was set")))))

(def-serial uncomment
  "Uncomment a line
     (uncomment \"/etc/ssh/sshd_config\" \"PermitRootLogin\" \"#\") 
  "
  [dest k c]
  (letfn [(strip [line]
            (if (.startsWith line (str c k))
              (.substring line 1)
              line))]
    (if-not (fs/exists? dest)
      (error (<< "~{dest} not found"))
      (let [lines (slurp dest)
            edited (map strip (clojure.string/split-lines lines))]
        (spit dest (clojure.string/join "\n" edited))
        (success "Uncommented line")))))
