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
    (assert (#{:present :absent} state))
    (let [states {:present lazy-mkdir :absent lazy-rm}]
      (coherce ((states state))))))

(def-serial file
  "A file resource:
   ; touch a file
   (file \"/tmp/1\" :present)
   ; delete a file
   (file \"/tmp/1\" :absent)
  "
  [path state]
  (letfn [(touch [f]
            (if (fs/exists? f) true (fs/touch f)))
          (delete [f]
                  (if (fs/exists? f) (fs/delete f) true))]
    (assert (#{:present :absent} state))
    (let [states {:present touch :absent delete}]
      (coherce ((states state) path)))))

(def-serial symlink
  "Symlink a file:
     ; create a new symlink
     (symlink \"/home/re-ops/.minimal-zsh/.zshrc\" \"/home/re-ops/.zshrc\") "
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
  "Template resource:
    ; apply a template and create a file:
    (template \"/tmp/resources/templates/lxd/preseed.mustache\" \"/tmp/preseed.yaml\" {})
  "
  [tmpl dest args]
  (let [source (slurp tmpl)
        out (render source args)
        parent (fs/parent dest)]
    (if (not (fs/exists? parent))
      (failure (<< "~{parent} missing, cannot spit template into ~{dest}"))
      (do
        (spit dest out)
        (success (<< "created file from template under ~{dest}"))))))

(def-serial copy
  "Copy a local file:
    (copy \"/tmp/foo\" \"/tmp/bla\")
  "
  [src dest]
  (try
    (coherce (= (fs/copy src dest) dest))
    (catch Exception e
      (failure (.getMessage e)))))

(def-serial rename
  "Move a local file:
    (rename \"/tmp/foo\" \"/tmp/bla\")
  "
  [src dest]
  (try
    (coherce (fs/rename src dest))
    (catch Exception e
      (failure (.getMessage e)))))

(def-serial chmod
  "Change file/directory mode resource:
    (chmod \"/home/re-ops/.ssh\" \"0777\")
    (chmod \"/home/re-ops/.ssh\" \"0777\" :recursive true)
    (chmod \"/home/re-ops/.ssh\" \"0777\" :recursive true :sudo true)
  "
  [dest mode opts]
  (letfn [(chmod-script []
            (let [options (if (:recursive opts) "-R" "")
                  sudo  (if (opts :sudo?) "1" "0")]
              (script
               (set! ID @("id" "-u"))
               (if (= "0" $ID)
                 ("/usr/bin/chmod" ~mode ~dest ~options)
                 (if (= "0" ~sudo)
                   ("/usr/bin/chmod" ~mode ~dest ~options)
                   ("sudo" "/usr/bin/chmod" ~mode ~dest ~options))))))]
    (run- chmod-script)))

(def-serial chown
  "Change file/directory owner using uid & gid resource:
      ; change file/folder ownership using user/group
      (chown \"/home/re-ops/.ssh\" \"foo\" \"bar\")
      ; change file/folder ownership recursively
      (chown \"/home/re-ops/.ssh\" \"foo\" \"bar\" {:recursive true})
      ; change file/folder ownership recursively with sudo
      (chown \"/home/re-ops/.ssh\" \"foo\" \"bar\" {:recursive true :sudo true})"
  [dest user group opts]
  (letfn [(chown-script []
            (let [u-g (<< "~{user}:~{group}")
                  options (if (:recursive opts) "-R" "")
                  sudo (if (:sudo? opts) "1" "0")]
              (script
               (set! ID @("id" "-u"))
               (if (= "0" $ID)
                 ("/usr/bin/chown" ~u-g ~dest ~options)
                 (if (= "0" ~sudo)
                   ("/usr/bin/chown" ~u-g ~dest ~options)
                   ("sudo" "/usr/bin/chown" ~u-g ~dest ~options))))))]
    (run- chown-script)))

(def-serial line
  "File line resource either append or remove lines:

    (line \"/tmp/foo\" \"bar\" :present); append line
    (line \"/tmp/foo\" (line-eq \"bar\") :absent); remove all lines equal to bar from the file
    (line \"/tmp/foo\" (fn [_ curr] (> 5 (.length curr))) :absent); remove lines using a function
    (line \"/tmp/foo\" \"bar\" :replace :with \"foo\"); replace lines equal to bar with foo
    (line \"/tmp/foo\" (fn [i _] (= i 2))  :replace :with \"foo\"); replace line in position 2 with value
    (line \"/tmp/foo\" (fn [i _] (= i 2))  :comment :with \"#\"); comment line in position 2
  "
  [file v state & {:keys [with]}]
  (letfn [(line-eq [line] (fn [_ curr] (not (= curr line))))
          (add-line [line]
                    (let [contents (slurp file)]
                      (if (clojure.string/includes? contents line)
                        (success "file already contains line")
                        (do
                          (spit file (str line "\n") :append true)
                          (success "line added to file")))))

          (replace-line [target]
                        (let [pred (if (string? target) (comp not (line-eq target)) target)
                              contents (slurp file)
                              replace-with (fn [i line] (if (pred i line) with line))
                              filtered (map-indexed replace-with
                                                    (clojure.string/split-lines contents))
                              output (clojure.string/join "\n" filtered)]
                          (if-not (= contents output)
                            (do
                              (spit file output)
                              (success "line replaced in file"))
                            (success "line not present in file"))))

          (rm-line [target]
                   (let [pred (if (string? target) (line-eq target) target)
                         contents (slurp file)
                         filtered (keep-indexed pred (clojure.string/split-lines contents))
                         output (clojure.string/join "\n" filtered)]
                     (if-not (= contents output)
                       (do
                         (spit file output)
                         (success "line removed from file"))
                       (success "line not present in file"))))]
    (let [states {:present add-line :absent rm-line :replace replace-line}]
      (assert (into #{} (keys states)) state)
      ((states state) v))))

(def-serial uncomment
  "Uncomment a line with separator value:
    (uncomment \"/etc/ssh/sshd_config\" \"PermitRootLogin\" \"#\")
  "
  [dest k sep]
  (if-not (fs/exists? dest)
    (error (<< "~{dest} not found"))
    (let [lines (slurp dest)
          edited (clojure.string/replace-first lines (str sep k) k)]
      (spit dest edited)
      (success "line was uncommented"))))

(def-serial uncomment-nth
  "Uncomment a line number nth with separator:
    (uncomment-nth \"/etc/ssh/sshd_config\" \"#\" 1)
  "
  [dest sep n]
  (if-not (fs/exists? dest)
    (error (<< "~{dest} not found"))
    (let [lines (clojure.string/split-lines (slurp dest))
          edited (map-indexed
                  (fn [i line]
                    (if-not (= i n)
                      line
                      (clojure.string/replace-first line sep ""))) lines)]
      (spit dest (clojure.string/join "\n" edited))
      (success "line was uncommented at position"))))

(def-serial line-set
  "Set an existing line value:
    (line-set \"/etc/ssh/sshd_config\" \"PermitRootLogin\" \"no\" \" \")
  "
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

(def-serial edn-set
  "Set a value in an edn file:
     (edn-set \"/tmp/secrets.edn\" [:lxc :pass] \"password\")
  "
  [dest ks v]
  (let [data (clojure.edn/read-string (slurp dest))]
    (if (= (get-in data ks) v)
      (success "value is already set")
      (do
        (spit dest (with-out-str (pr (assoc-in data ks v))))
        (success "value was set in file")))))

(def-serial yaml-set
  "Set a value in a yaml file:
     (yaml-set \"/tmp/test.yaml\" [:parent :key] \"value\")
  "
  [dest ks v]
  (let [data (yaml/parse-string (slurp dest))]
    (if (= (get-in data ks) v)
      (success "value is already set")
      (do
        (spit dest
              (yaml/generate-string
               (assoc-in data ks v) :dumper-options {:flow-style :block}))
        (success "value was set in file")))))

(def-serial replace-all
  "Replace all occurrences of a Regex match in a file
    (replace-all \"/etc/apt/sources.list\" \"us.\" \"local.\")
  "
  [dest match with]
  (letfn [(edit [line]
            (let [post (clojure.string/replace line (re-pattern match) with)]
              {:updated? (not (= post line)) :line post}))]
    (if-not (fs/exists? dest)
      (error (<< "~{dest} not found"))
      (let [lines (slurp dest)
            edited (map edit (clojure.string/split-lines lines))
            updated (count (filter :updated? edited))]
        (spit dest (clojure.string/join "\n" (map :line edited)))
        (success (<< "~{updated} lines were updated in ~{dest}"))))))
