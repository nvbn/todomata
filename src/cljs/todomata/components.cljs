(ns todomata.components
  (:require [om.core :as om :include-macros true]
            [sablono.core :refer-macros [html]]
            [om-tools.core :refer-macros [defcomponent]]))

(defcomponent welcome-page
  "Welcome page for non-authenticated users."
  [_ _]
  (display-name [_] "WelcomePage")
  (render [_] (html [:h1 "Welcome to todomata!"])))

(defcomponent main
  "Main component."
  [app owner]
  (display-name [_] "Main")
  (render [_] (html [:div.container
                     (if (:user app)
                       [:h1 "Authenticated"]
                       (om/build welcome-page app))])))

(defn init!
  [state]
  (om/root main state
           {:target (js/document.getElementById "main")}))
