(ns collabj.core
  (:require
    [collabj.dom :as dom]
    [collabj.gfx :as gfx]
    [clojure.string :as string]
    [cljs.reader :as edn]))

(enable-console-print!)

(defn now [] (.getTime (js/Date.)))

(def current-state (atom
             {:mouse {:time 0 :x 0 :y 0 :pressed false}
              :drawings []}))

(def server (atom nil))

(defn line-mouse-move [state t x y]
  (let [ox (get-in state [:mouse :x])
        oy (get-in state [:mouse :y])]
    (if (> (- t (get-in state [:mouse :time])) 50)
      (-> state
          (update-in [:mouse]
                     #(assoc % :x x :y y :time t))
          (update-in [:drawings]
                     #(conj % [ox oy x y])))
      state)))

(defn paint [drawing]
  (apply gfx/line (concat [(.-ctx js/window)] drawing)))

(defn draw []
  (doseq [item (:drawings @current-state)]
    (let [msg (clojure.string/join (interpose " " (map str item)))]
      (.send @server msg)))
  (swap! current-state assoc :drawings []))


(defn mouse-x [element event]
  (int
    (+ (- (.-clientX event)
          (.-left (.getBoundingClientRect element))
          (.-clientLeft element))
       (.-scrollLeft element))))
(defn mouse-y [element event]
  (int
    (+ (- (.-clientY event)
          (.-top (.getBoundingClientRect element))
          (.-clientTop element))
       (.-scrollTop element))))

(defn go-board [board]
  (set! (.-location dom/window) (str "/board/" board)))

(defn index-main []
  (let [[inp btn] (map dom/by-id ["board-input" "board-btn"])]
    (letfn [(get-board [] (.-value inp))]
      (dom/listen btn "click" #(go-board (get-board)))
      (dom/listen inp "keypress"
                  (fn [ev]
                    (if (= 13 (.-keyCode ev))
                      (go-board (get-board))))))))

(defn board-main []
  (def config
    (-> "config"
        (dom/by-id)
        (dom/attr "data-edn")
        (edn/read-string)))
  (def canvas (dom/by-id "drawing-board"))
  (def ctx (gfx/context canvas))
  (set! (.-ctx js/window) ctx)

  (println "config is: " config)

  (swap! server (fn [_]
                  (js/WebSocket. (str "ws://localhost:8080/ws/" (:board config)))))
  (set! (.-onmessage @server)
        (fn [message]
          (let [l (map js/parseInt
                       (clojure.string/split
                         (.-data message) #" "))]
            (paint (vec l)))))


  (gfx/set-color ctx "green")

  (dom/listen canvas "mousemove"
              (fn [ev]
                (if (= (.-buttons ev) 1)
                  (let [x (mouse-x canvas ev)
                        y (mouse-y canvas ev) t (now)]
                    (if (get-in @current-state [:mouse :pressed])
                      (swap! current-state line-mouse-move t x y)
                      (swap! current-state
                             (fn [s]
                               (update-in s [:mouse]
                                          #(assoc % :pressed true :time t :x x :y y))))))
                  (swap! current-state assoc-in [:mouse :pressed] false))
                (draw))))

(defn main []

  (if (not (nil? (dom/by-id "drawing-board")))
    (board-main)
    (index-main)))

(set! (.-onload dom/window) main)

