(ns todomata.api-test
  (:require [clojure.test :refer [deftest is use-fixtures testing]]
            [clj-time.core :as clj-time]
            [todomata.models.core-test :refer [db-fixtures]]
            [todomata.models.core :as m]
            [todomata.utils :as u]
            [todomata.api :as a]))

(defn wrap-authentication
  [f]
  (with-redefs [u/get-user-id (constantly "owner")]
    (f)))

(apply use-fixtures :each wrap-authentication db-fixtures)

(deftest test-get-tasks
  (let [task (m/create-task! {:owner-id "owner"
                              :description "test"})]
    (with-redefs [clj-time/now (constantly (clj-time/date-time 2001 1 1))]
      (let [tasks [task (m/create-task! {:owner-id "owner"
                                         :description "description"})]]
        (m/create-task! {:owner-id "other-owner"
                         :description "description"})
        (is (= (a/get-tasks {})
               {:status 200
                :body tasks}))))))

(deftest test-create-task!
  (let [{:keys [status body]} (a/create-task! {:body {:description "task description"}})]
    (is (= status 201))
    (is (= (dissoc body :updated :created :task-id)
           {:to nil
            :from nil
            :deleted false
            :done false
            :owner-id "owner"
            :description "task description"}))))

(deftest test-update-task!
  (testing "when user task"
    (let [{:keys [task-id created]} (m/create-task! {:description "first description"
                                                     :owner-id "owner"})
          {:keys [status body]} (a/update-task! {:body {:description "new description"}}
                                                task-id)]
      (is (= status 200))
      (is (= (dissoc body :updated)
             {:to nil
              :from nil
              :deleted false
              :done false
              :description "new description"
              :created created
              :task-id task-id
              :owner-id "owner"}))))
  (testing "when other user task"
    (let [{:keys [task-id]} (m/create-task! {:description "first description"
                                             :owner-id "other-owner"})
          {:keys [status body]} (a/update-task! {:body {:description "new description"}}
                                                task-id)]
      (is (= status 404))
      (is (nil? body))))
  (testing "when task doesn't exists"
    (let [{:keys [status body]} (a/update-task! {} "5427828d2318d4ede8f2e6cf")]
      (is (= status 404))
      (is (nil? body)))))

(deftest test-delete-task!
  (testing "when user task"
    (let [{:keys [task-id]} (m/create-task! {:description "first description"
                                             :owner-id "owner"})
          {:keys [status]} (a/delete-task! {} task-id)]
      (is (= status 204))))
  (testing "when other user task"
    (let [{:keys [task-id]} (m/create-task! {:description "first description"
                                             :owner-id "other-owner"})
          {:keys [status]} (a/delete-task! {} task-id)]
      (is (= status 404))))
  (testing "when task doesn't exists"
    (let [{:keys [status]} (a/delete-task! {} "5427828d2318d4ede8f2e6cf")]
      (is (= status 404)))))
