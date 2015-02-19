(ns collabj.views
  (:use hiccup.page hiccup.core))

(def scripts
  (html [:script { :src "gen/main.js" }]))

(defn css
  [filename]
  (html [:link { :rel "stylesheet" :href filename :type "text/css" :media "screen" }]))

(defn index
  []
  (html5
    [:head
     [:title "Collabj"]
     (css "style.css")]
    [:body
     [:h1 "Collabj!"]
     [:div#board-container
      [:canvas#drawing-board
       {:width 500 :height 500}]]
     scripts]))
