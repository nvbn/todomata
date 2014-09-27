(ns todomata.models.presentation-test
  (:require [clojure.test :refer [deftest is use-fixtures testing]]
            [clj-time.core :refer [date-time]]
            [clojurewerkz.elastisch.rest.document :as esd]
            [clojurewerkz.elastisch.query :as q]
            [environ.core :refer [env]]
            [todomata.const :as const]
            [todomata.models.presentation :as p]))

(defn wrap-db
  [f]
  (p/init-elastic!)
  (f))

(defn clear-db
  [f]
  (esd/delete-by-query @p/elastic (env :elastic-index)
                       const/tasks-index (q/match-all))
  (f))

(use-fixtures :once wrap-db)

(use-fixtures :each clear-db)

(defn- make-task
  [& params]
  (let [task {:task-id "task-id"
              :owner-id "owner-id"
              :description "description"
              :created (date-time 2010 10 12 10 30)
              :updated (date-time 2011 10 12 10 30)
              :from nil
              :to nil
              :done false
              :deleted false}]
    (if (seq params)
      (apply assoc task params)
      task)))

(deftest test-prepare-to-put
  (is (= (p/prepare-to-put (make-task))
         {:task-id "task-id"
          :owner-id "owner-id"
          :description "description"
          :created "2010.10.12 10:30"
          :updated "2011.10.12 10:30"
          :from nil
          :to nil
          :done false
          :deleted false})))

(deftest test-prepare-from-db
  (let [task (make-task)]
    (is (= (p/prepare-from-db (p/prepare-to-put task))
           task))))

(deftest test-put-task!
  (let [task (make-task)]
    (p/put-task! (make-task :done true))
    (p/put-task! task)
    (Thread/sleep 1000)                                     ; wait for elasticsearch
    (is (= [task]
           (p/->sources (esd/search @p/elastic (env :elastic-index) const/tasks-index
                                    :filter (q/term :task-id (:task-id task))))))))

(deftest test-get-user-tasks
  (dotimes [x 2] (p/put-task! (make-task :owner-id "other"
                                         :task-id (str "id-1-" x))))
  (dotimes [x 5] (p/put-task! (make-task :task-id (str "id-2-" x))))
  (dotimes [x 3] (p/put-task! (make-task :done true
                                         :task-id (str "id-3-" x))))
  (dotimes [x 9] (p/put-task! (make-task :done true
                                         :task-id (str "id-4-" x)
                                         :description (str "magic text " x))))
  (dotimes [x 1] (p/put-task! (make-task :done true
                                         :deleted true
                                         :task-id (str "id-5-" x))))
  (Thread/sleep 1000)                                       ; wait for elasticsearch
  (is (= 5 (count (p/get-user-tasks "owner-id"))))
  (is (= 12 (count (p/get-user-tasks "owner-id"
                                     :done true))))
  (is (= 1 (count (p/get-user-tasks "owner-id"
                                    :done true
                                    :deleted true)))))
