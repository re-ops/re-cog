(ns re-cog.resources.openssl
  "Openssl cert generation utilities"
  (:require
   [re-cog.common.functions :refer (require-functions)]
   [re-cog.common.constants :refer (require-constants)]
   [re-cog.common.resources :refer (run-)]
   [re-cog.common.defs :refer (def-serial)]))

(require-functions)
(require-constants)

(def-serial generate-cert
  "Generat ssl certs"
  [host dest]
  (letfn [(certs []
            (let [subj (<< "'/C=pp/ST=pp/L=pp/O=pp Inc/OU=DevOps/CN=~{host}/emailAddress=re-ops@~{host}'")
                  keyout (<< "~{dest}/~{host}.key")
                  crt (<< "~{dest}/~{host}.crt")]
              (script
               (~openssl-bin "req" "-x509" "-nodes" "-days" "365" "-newkey" "rsa:2048" "-keyout" ~keyout "-out" ~crt "-subj" ~subj))))
          (dht []
               (let [pem (<< "~{dest}/dhparam.pem")]
                 (script
                  (~openssl-bin "dhparam" "-dsaparam" "-out" ~pem "4096"))))]
    (run- certs)
    (run- dht)))
