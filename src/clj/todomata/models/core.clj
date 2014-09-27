(ns todomata.models.core
  (:require [todomata.models.presentation :as p]
            [todomata.models.changes :as c]))

(defn init!
  []
  (c/init-mongo!)
  (p/init-elastic!))

(defn create-task!
  "Create task, put in changes and update presentation."
  [data]
  (let [{:keys [created task-data] :as task} (c/create-task! data)])
  (-> (c/create-task! data)
      c/get-task-id
      c/get-task
      p/put-task!))

(defn update-task!
  "Update task, put in changes and update presentation."
  [task-id data]
  (c/update-task! task-id (dissoc data :task-id))
  (p/put-task! (c/get-task task-id)))

(def get-user-tasks p/get-user-tasks)
