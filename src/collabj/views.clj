(ns collabj.views
  (:use hiccup.page))

(defn index
  []
  (html5 [:body, [:p "Hello world!"]]))
