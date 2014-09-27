(ns todomata.models.presentation
  (:require [environ.core :refer [env]]
            [clojurewerkz.elastisch.rest :as esr]
            [clojurewerkz.elastisch.rest.index :as esi]
            [clojurewerkz.elastisch.rest.document :as esd]
            [clojurewerkz.elastisch.query :as q]
            [clj-time.format :as f]
            [todomata.const :as const]))

(def elastic (atom nil))

(def date-format "yyyy.MM.dd HH:mm")

(def date-formatter (f/formatter date-format))

(def elastic-task-mapping
  {:properties {:owner-id {:type "string"
                           :index "not_analyzed"}
                :task-id {:type "string"
                          :index "not_analyzed"}
                :description {:type "string"}
                :depending {:properties {:task-id {:type "string"
                                                   :index "not_analyzed"}}}
                :created {:type "date"
                          :format date-format}
                :updated {:type "date"
                          :format date-format}
                :from {:type "date"
                       :format date-format}
                :to {:type "date"
                     :format date-format}
                :location {:type "string"}
                :done {:type "boolean"}
                :deleted {:type "boolean"}}})

(defn create-index!
  []
  (esi/create @elastic (env :elastic-index)
              :mappings {const/tasks-index elastic-task-mapping}))

(defn init-elastic!
  []
  (reset! elastic (esr/connect))
  (try (create-index!)
       (catch Exception _)))

(def date-fields [:created :updated :from :to])

(defn- unparse-or-nothing
  "Try to unparse date or do nothing."
  [date]
  (when date
    (try (f/unparse date-formatter date)
         (catch Exception _ date))))

(defn prepare-to-put
  "Prepare task to put in index."
  [task]
  (reduce #(update-in %1 [%2] unparse-or-nothing) task
          date-fields))

(defn- parse-or-nothing
  "Try to parse date or do nothing."
  [date]
  (when date
    (try (f/parse date-formatter date)
         (catch Exception _ date))))

(defn prepare-from-db
  "Prepare task received from db."
  [task]
  (reduce #(update-in %1 [%2] parse-or-nothing) task
          date-fields))

(defn put-task!
  "Removes previous indexed task with same `task-id` and puts task in elastic index."
  [task]
  (esd/delete-by-query @elastic (env :elastic-index)
                       const/tasks-index (q/term :task-id (:task-id task)))
  (let [prepared (prepare-to-put task)
        {:keys [_id]} (esd/create @elastic (env :elastic-index)
                                  const/tasks-index prepared)]
    (prepare-from-db (assoc prepared :_id _id))))

(defn ->sources
  "Get source documents from elastic search result."
  [result]
  (->> result
       :hits
       :hits
       (map :_source)
       (map prepare-from-db)))

(defn get-user-tasks
  "Get tasks for user."
  [user-id & {:keys [sorting done deleted query]
              :or {sorting {:created "desc"} done false deleted false query nil}}]
  (let [must (atom [(q/term :owner-id user-id)
                    (q/term :done done)
                    (q/term :deleted deleted)])]
    (when query
      (swap! must conj (q/fuzzy-like-this :like-text query
                                          :fields [:description :location])))
    (->sources (esd/search @elastic (env :elastic-index) const/tasks-index
                           :filter (q/bool :must @must)
                           :size const/result-size))))
