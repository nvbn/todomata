(ns todomata.handlers
  (:require [compojure.handler :refer [site]]
            [hiccup.middleware :refer [wrap-base-url]]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.transit :refer [wrap-transit-response]]
            [ring.util.codec :refer [form-decode]]
            [cemerick.friend :refer [authenticate]]
            [friend-oauth2.workflow :refer [workflow]]
            [friend-oauth2.util :refer [format-config-uri get-access-token-from-params]]
            [environ.core :refer [env]]
            [todomata.const :as const]
            [todomata.routes :refer [main]]))

(def github-client-config
  {:client-id (env :github-client-id)
   :client-secret (env :github-client-secret)
   :callback {:domain (env :github-client-domain)
              :path const/login-uri}})

(def github-uri-config
  {:authentication-uri {:url "https://github.com/login/oauth/authorize"
                        :query {:client_id (:client-id github-client-config)
                                :response_type "code"
                                :redirect_uri (format-config-uri github-client-config)
                                :scope ""}}

   :access-token-uri {:url "https://github.com/login/oauth/access_token"
                      :query {:client_id (:client-id github-client-config)
                              :client_secret (:client-secret github-client-config)
                              :grant_type "authorization_code"
                              :redirect_uri (format-config-uri github-client-config)
                              :code ""}}})

(def github-workflow
  (workflow
    {:client-config github-client-config
     :uri-config github-uri-config
     :config-auth {:roles #{:user}}
     :access-token-parsefn get-access-token-from-params}))

(def app (-> (authenticate main
                           {:allow-anon? true
                            :default-landing-uri const/landing-uri
                            :login-uri const/login-uri
                            :workflows [github-workflow]})
             site
             (wrap-transit-response {:encoding :json})
             wrap-base-url
             wrap-reload))

(defn init
  [])
