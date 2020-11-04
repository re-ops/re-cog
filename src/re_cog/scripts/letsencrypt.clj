(ns re-cog.scripts.letsencrypt
  (:require
   [pallet.stevedore :refer (script)]
   [clojure.core.strint :refer (<<)]))

(defn update-certs
  "Update certificates using dns-01 challenge"
  [user token]
  (fn []
    (script
     ("export" (set! PROVIDER "cloudflare"))
     ("export" (set! LEXICON_CLOUDFLARE_USERNAME ~user))
     ("export" (set! LEXICON_CLOUDFLARE_TOKEN ~token))
     ("/srv/dehydrated/dehydrated" "--register" "--accept-terms")
     ("/srv/dehydrated/dehydrated" "--cron" "--hook" "/srv/dehydrated/dehydrated.default.sh" "--challenge" "dns-01"))))

(defn apply-domains
  "Set the domains for which certificates will be generated"
  [domains]
  (fn []
    (script
     ("/usr/bin/rm" "/srv/dehydrated/domains.txt")
     (doseq [d ~domains]
       ("echo" @d >> "/srv/dehydrated/domains.txt")))))
