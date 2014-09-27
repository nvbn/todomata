(ns todomata.views-test
  (:require [clojure.test :refer [deftest is]]
            [todomata.views :as v]))

(deftest index-test
  (is (v/dashboard)))
