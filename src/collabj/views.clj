(ns collabj.views
  (:use hiccup.page hiccup.core))

(def scripts
  (html [:script { :src "gen/main.js" }]))

(defn index
  []
  (html5
    [:body
     [:p "Hello world!"]
     scripts]))
