(ns re-cog.recipes.desktop
  "Desktop related setup")

#_(defn chrome
    "Google chrome setup"
    []
    (let [repo "deb [arch=amd64] http://dl.google.com/linux/chrome/deb/ stable main"
          key "https://dl-ssl.google.com/linux/linux_signing_key.pub"]
      (add-repo repo key "7FAC5991")
      (package "google-chrome-stable")))
