(ns todomata.api
  (:require [todomata.models.core :as m]
            [todomata.utils :as u]))

(defn get-tasks
  "Api resource for getting tasks."
  [request]
  (let [user-id (u/get-user-id request)]
    {:status 200
     :body (m/get-user-tasks user-id)}))

(defn create-task!
  "Api resource for creating new task."
  [request]
  {:status 201
   :body (m/create-task! (assoc (:body request)
                           :owner-id (u/get-user-id request)))})

(defmacro when-owner
  "Do cation or return 404 if not owner."
  [task-id user-id & body]
  `(if (m/is-user-task? ~task-id ~user-id)
     (do ~@body)
     {:status 404}))

(defn update-task!
  "Api resource for updating exists task."
  [request task-id]
  (when-owner task-id (u/get-user-id request)
    {:status 200
     :body (m/update-task! task-id (:body request))}))

(defn delete-task!
  "Api resource for deleting task."
  [request task-id]
  (when-owner task-id (u/get-user-id request)
    (m/update-task! task-id {:deleted true})
    {:status 204}))
