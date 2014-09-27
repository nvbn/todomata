(ns todomata.models
  (:require [monger.core :as mg]
            [monger.collection :as mc]
            [environ.core :refer [env]]
            [todomata.const :as const])
  (:import org.bson.types.ObjectId))

(def mongo (atom nil))

(def mongo-db (atom nil))

(defn init-mongo!
  []
  (reset! mongo (mg/connect))
  (reset! mongo-db (mg/get-db @mongo (env :mongo-db))))

(defn create-task!
  [data]
  (mc/insert-and-return @mongo-db const/changes-collection
                        {:type :create
                         :data data}))

(defn update-task!
  [task-id changes]
  (mc/insert-and-return @mongo-db const/changes-collection
                        {:type :update
                         :task-id task-id
                         :data changes}))

(defn get-task
  [task-id]
  (let [original (mc/find-one @mongo-db const/changes-collection
                              {:_id (ObjectId. task-id)})
        changes (mc/find-maps @mongo-db const/changes-collection
                         {:task-id task-id})
        task (loop [changes (map :data changes)
                    task (:data original)]
               (if (seq changes)
                 (recur (rest changes) (merge task (first changes)))
                 task))]
    (assoc task :task-id task-id)))
