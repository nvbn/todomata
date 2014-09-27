(ns todomata.core
  (:require [todomata.components :as components]))

(defn ^:export run
  []
  (let [state (atom {})]
    (components/init! state)))
