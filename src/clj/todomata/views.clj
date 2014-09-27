(ns todomata.views
  (:require [hiccup.page :refer [html5 include-css include-js]]
            [environ.core :refer [env]]
            [todomata.const :as const]))

(defn- assets
  [debug production]
  (map #(str const/static-url %)
       (if (env :is-debug) debug production)))

(def js (assets ["components/jquery/dist/jquery.js"
                 "components/bootstrap/dist/js/bootstrap.js"
                 "components/react/react.js"
                 "cljs-target/goog/base.js"
                 "main.js"]
                ["components/jquery/dist/jquery.min.js"
                 "components/bootstrap/dist/js/bootstrap.min.js"
                 "components/react/react.min.js"
                 "main.js"]))

(def css (assets ["components/bootstrap/dist/css/bootstrap.css"
                  "components/bootstrap/dist/css/bootstrap-theme.css"
                  "components/font-awesome/css/font-awesome.css"
                  "main.css"]
                 ["components/bootstrap/dist/css/bootstrap.min.css"
                  "components/bootstrap/dist/css/bootstrap-theme.min.css"
                  "components/font-awesome/css/font-awesome.min.css"
                  "main.css"]))

(defmacro page
  [& {:keys [title head body]}]
  `(html5 [:head
           [:title ~title]
           (apply include-js js)
           (apply include-css css)
           ~@head]
          [:body ~@body]))

(defn welcome
  "Welcome page for non-authenticated users."
  []
  (page :body [[:div.container
                [:h1 "Welcome to todomata!"]
                [:a.btn.btn-primary {:href "/login/"}
                 [:i.fa.fa-github] " Login with github"]]]))

(defn dashboard
  "Dashboard page for authenticated users."
  []
  (page :head [(when (env :is-debug)
                 [:script "goog.require('todomata.core');"])]
        :body [[:div#main]
               [:script "todomata.core.run();"]]))
