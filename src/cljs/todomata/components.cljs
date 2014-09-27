(ns todomata.components
  (:require [om.core :as om :include-macros true]
            [sablono.core :refer-macros [html]]
            [om-tools.core :refer-macros [defcomponent]]
            [todomata.const :as const]))

(defcomponent header
  "Component for page header"
  [_ _]
  (display-name [_] "Header")
  (render [_] (html [:div.container
                     [:a.btn.btn-default.pull-right {:href const/logout-uri} "Logout"]
                     [:h1 "TODOmata"]])))

(defcomponent main
  "Main component."
  [app owner]
  (display-name [_] "Main")
  (render [_] (html [:div.container
                     (om/build header app)])))

(defn init!
  [state]
  (om/root main state
           {:target (js/document.getElementById "main")}))
