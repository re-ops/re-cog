(ns re-cog.resources.nginx
  "Nginx related resources"
  (:require
   [re-cog.common.defs :refer (def-serial)]
   [re-cog.common.functions :refer (require-functions)]
   [re-cog.common.constants :refer (require-constants)]))

(require-functions)
(require-constants)

(def-serial site-enabled
  "An nginx site enabled with ssl and optional basic auth"
  [nginx name external internal basic-auth?]
  (let [source (slurp (io/resource "main/resources/site.conf"))
        m {:fqdn (fqdn) :external-port external :internal-port internal :basic-auth basic-auth?}
        out (render source m)
        {:keys [enabled]} nginx]
    (spit (<< "~{enabled}/~{name}.conf")  out)
    (success (<< "enabled site added"))))
