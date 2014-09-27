(ns todomata.models.core-test
  (:require [clojure.test :refer [deftest is use-fixtures]]
            [todomata.models.changes-test :as ct]
            [todomata.models.presentation-test :as pt]
            [todomata.models.core :as m]))

(use-fixtures :once ct/wrap-db)

(use-fixtures :once pt/wrap-db)

(use-fixtures :each pt/clear-db)

(deftest test-create-task!
  (is (= (dissoc (m/create-task! {:description "about cats"
                                  :owner-id "owner"})
                 :created :updated :_id :task-id)
         {:description "about cats"
          :from nil
          :owner-id "owner"
          :to nil})))

(deftest test-update-task!
  (let [{:keys [task-id]} (m/create-task! {:description "new about cats"
                                           :owner-id "owner"})]
    (is (= (dissoc (m/update-task! task-id {:description "re: new"})
                   :_id :updated :created)
           {:to nil
            :from nil
            :task-id task-id
            :description "re: new"
            :owner-id "owner"}))))
