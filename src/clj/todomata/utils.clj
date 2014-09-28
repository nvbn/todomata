(ns todomata.utils
  (:require [cemerick.friend :as friend]))

(defn get-user-id
  "Get user id if possible."
  [request]
  (some-> (friend/identity request)
          :current))
