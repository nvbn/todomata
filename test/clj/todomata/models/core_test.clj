(ns todomata.models.core-test
  (:require [clojure.test :refer [deftest is use-fixtures]]
            [todomata.models.changes-test :as ct]
            [todomata.models.presentation-test :as pt]
            [todomata.models.core :as m]))

(def db-fixtures [ct/wrap-db ct/clear-db pt/wrap-db pt/clear-db])

(apply use-fixtures :each db-fixtures)

(deftest test-create-task!
  (is (= (dissoc (m/create-task! {:description "about cats"
                                  :owner-id "owner"})
                 :created :updated :_id :task-id)
         {:description "about cats"
          :from nil
          :owner-id "owner"
          :to nil
          :done false
          :deleted false})))

(deftest test-update-task!
  (let [{:keys [task-id]} (m/create-task! {:description "new about cats"
                                           :owner-id "owner"})]
    (is (= (dissoc (m/update-task! task-id {:description "re: new"})
                   :_id :updated :created)
           {:to nil
            :from nil
            :done false
            :deleted false
            :task-id task-id
            :description "re: new"
            :owner-id "owner"}))))
