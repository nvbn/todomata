(ns todomata.routes
  (:require [compojure.core :refer [defroutes GET ANY]]
            [compojure.route :refer [resources]]
            [ring.util.response :refer [redirect]]
            [cemerick.friend :as friend]
            [todomata.const :as const]
            [todomata.views :as v]))

(defroutes main
  (GET "/" request (if (friend/identity request)
                     (v/dashboard)
                     (v/welcome)))
  (friend/logout
    (ANY const/logout-uri request (redirect "/")))
  (resources const/static-url))
