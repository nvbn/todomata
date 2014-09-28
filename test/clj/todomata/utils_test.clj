(ns todomata.utils-test
  (:require [clojure.test :refer [deftest is testing]]
            [cemerick.friend :as friend]
            [todomata.utils :as u]))

(deftest test-get-user-id
  (testing "when user authenticated"
    (with-redefs [friend/identity (constantly {:current "user-id"})]
      (is (= (u/get-user-id nil) "user-id"))))
  (testing "when anonymouse"
    (with-redefs [friend/identity (constantly nil)]
      (is (nil? (u/get-user-id nil))))))
