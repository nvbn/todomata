(ns todomata.models-test
  (:require [clojure.test :refer [deftest is use-fixtures]]
            [todomata.models :as m]))

(defn wrap-db
  [f]
  (m/init-mongo!)
  (f))

(use-fixtures :once wrap-db)

(deftest test-create-task!
  (is (= (:data (m/create-task! {:title "test"})) {:title "test"})))

(deftest test-update-task!
  (let [task (m/create-task! {:title "new task"})
        task-id (-> task :_id .toString)]
    (is (= (dissoc (m/update-task! task-id {:description "description"}) :_id :created)
           {:data {:description "description"}
            :task-id task-id
            :type :update}))))

(deftest test-get-task
  (let [task (m/create-task! {:title "new task"})
        task-id (-> task :_id .toString)]
    (m/update-task! task-id {:description "description"
                             :depend-on [12]})
    (m/update-task! task-id {:description "updated description"})
    (is (= (m/get-task task-id)
           {:task-id task-id
            :title "new task"
            :depend-on [12]
            :description "updated description"}))))
