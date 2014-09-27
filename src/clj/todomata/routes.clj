(ns todomata.routes
  (:require [compojure.core :refer [defroutes GET]]
            [compojure.route :refer [resources]]
            [cemerick.friend :as friend]
            [todomata.const :as const]
            [todomata.views :as v]))

(defroutes main
  (GET "/" request (if (friend/identity request)
                     (v/dashboard)
                     (v/welcome)))
  (resources const/static-url))
