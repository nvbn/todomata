(ns todomata.models
  (:require [monger.core :as mg]
            [monger.collection :as mc]
            monger.joda-time
            [environ.core :refer [env]]
            [clj-time.core :refer [now]]
            [todomata.const :as const])
  (:import org.bson.types.ObjectId))

(def mongo (atom nil))

(def mongo-db (atom nil))

(defn init-mongo!
  []
  (reset! mongo (mg/connect))
  (reset! mongo-db (mg/get-db @mongo (env :mongo-db))))

(defn create-task!
  "Creates new task."
  [data]
  (mc/insert-and-return @mongo-db const/changes-collection
                        {:type :create
                         :created (now)
                         :data data}))

(defn update-task!
  "Update exists task."
  [task-id changes]
  (mc/insert-and-return @mongo-db const/changes-collection
                        {:type :update
                         :created (now)
                         :task-id task-id
                         :data changes}))

(defn get-task
  "Get task combined from changes."
  [task-id]
  (let [original (mc/find-one-as-map @mongo-db const/changes-collection
                                     {:_id (ObjectId. task-id)
                                      :type :create})
        changes (mc/find-maps @mongo-db const/changes-collection
                              {:$query {:task-id task-id
                                        :type :update}
                               :$orderby {:created 1}})
        task (loop [changes (map :data changes)
                    task (:data original)]
               (if (seq changes)
                 (recur (rest changes) (merge task (first changes)))
                 task))]
    (assoc task :task-id task-id)))
