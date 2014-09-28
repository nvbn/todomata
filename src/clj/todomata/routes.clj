(ns todomata.routes
  (:require [compojure.core :refer [defroutes GET POST PUT DELETE ANY PATCH context]]
            [compojure.route :refer [resources]]
            [ring.util.response :refer [redirect]]
            [cemerick.friend :as friend]
            [todomata.const :as const]
            [todomata.views :as v]
            [todomata.api :as a]))

(defroutes api
  (GET "/tasks/" [request] (a/get-tasks request))
  (POST "/tasks/" [request] (a/create-task! request))
  (PUT "/tasks/:task-id/" [task-id :as request] (a/update-task! request task-id))
  (PATCH "/tasks/:task-id/" [task-id :as request] (a/update-task! request task-id))
  (DELETE "/tasks/:task-id/" [task-id :as request] (a/delete-task! request task-id)))

(defroutes main
  (GET "/" request (if (friend/identity request)
                     (v/dashboard)
                     (v/welcome)))
  (context "/api" request
           (friend/wrap-authorize api #{:user}))
  (friend/logout
    (ANY const/logout-uri request (redirect "/")))
  (resources const/static-url))
