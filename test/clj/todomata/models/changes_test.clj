(ns todomata.models.changes-test
  (:require [clojure.test :refer [deftest is use-fixtures testing]]
            [monger.collection :as mc]
            [todomata.models.changes :as m]
            [todomata.const :as const]))

(defn wrap-db
  [f]
  (m/init-mongo!)
  (f))

(defn clear-db
  [f]
  (mc/remove @m/mongo-db const/changes-collection)
  (f))

(use-fixtures :each wrap-db clear-db)

(deftest test-create-task!
  (is (= (:data (m/create-task! {:title "test"}))
         {:title "test"
          :deleted false
          :done false})))

(deftest test-update-task!
  (let [task (m/create-task! {:title "new task"})
        task-id (m/get-task-id task)]
    (is (= (dissoc (m/update-task! task-id {:description "description"}) :_id :created)
           {:data {:description "description"}
            :task-id task-id
            :type :update}))))

(deftest test-get-task
  (let [task (m/create-task! {:title "new task"})
        task-id (m/get-task-id task)]
    (m/update-task! task-id {:description "description"
                             :depend-on [12]})
    (m/update-task! task-id {:description "updated description"})
    (is (= (dissoc (m/get-task task-id) :created :updated)
           {:task-id task-id
            :title "new task"
            :depend-on [12]
            :description "updated description"
            :deleted false
            :done false}))))

(deftest test-is-user-task?
  (testing "when is user task"
    (let [task (m/create-task! {:owner-id "user"})]
      (is (true? (m/is-user-task? (m/get-task-id task)
                                  "user")))))
  (testing "when other user task"
    (let [task (m/create-task! {:owner-id "other-user"})]
      (is (false? (m/is-user-task? (m/get-task-id task)
                                  "user")))))
  (testing "when task doesn't exists"
    (is (false? (m/is-user-task? "54276082231862451571d91a"
                                 "user"))))
  (testing "with wrong task id"
    (is (false? (m/is-user-task? "wrong-id"
                                 "user")))))
