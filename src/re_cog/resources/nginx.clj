(ns re-cog.resources.nginx
  "Nginx related resources"
  (:require
   [re-cog.common.defs :refer (def-serial)]
   [re-cog.common.functions :refer (require-functions)]
   [re-cog.common.constants :refer (require-constants)]))

(require-functions)
(require-constants)

(def-serial site-enabled
  "An nginx site enabled with ssl and optional basicauth/websockets:
    ; by default options are false
    (site-enabled nginx \"grafana\" external-port 3000 {})
    ; enable basic auth
    (site-enabled nginx \"grafana\" external-port 3000 {:basic-auth true})
    ; enable websockets
    (site-enabled nginx \"grafana\" external-port 3000 {:websockets true})
  "
  [nginx name external internal opts]
  (let [{:keys [enabled]} nginx
        {:keys [basic-auth websockets] :or {basic-auth false websockets false tls-3 true} :as with-defaults} opts
        source (slurp (io/resource "main/resources/site.conf"))
        m (merge with-defaults {:fqdn (fqdn) :external-port external :internal-port internal})]
    (spit (<< "~{enabled}/~{name}.conf")  (render source m))
    (success (<< "enabled site added"))))
