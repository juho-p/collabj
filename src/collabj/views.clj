(ns collabj.views
  (:use hiccup.page hiccup.core))

(def scripts
  (html [:script { :src "/gen/main.js" }]))

(defn css
  [filename]
  (html [:link { :rel "stylesheet" :href filename :type "text/css" :media "screen" }]))

(def styles
  (css "/style.css"))

(def index
  (html5
    [:head
     [:title "Collabj"]
     styles]
    [:body
     [:h1 "Collabj!"]
     [:input#board-input {:type "text"}]
     [:button#board-btn "GO"]
     scripts]))

(defn board
  [config]
  (html5
    [:head
     [:title "Collabj"]
     styles]
    [:body
     [:div#config {:style "display:none" :data-edn (prn-str config)}]
     [:h1 "Collabj!"]
     [:div#board-container
      [:canvas#drawing-board
       {:width 500 :height 500}]]
     scripts]))
