(ns collabj.gfx)

(defn context [elem] (.getContext elem "2d"))

(defn rectangle [ctx color x y w h]
  (set! (.-fillStyle ctx) color)
  (.fillRect ctx x y w h))

(defn set-color [ctx color]
  (set! (.-strokeStyle ctx) color))

(defn line [ctx x1 y1 x2 y2]
  (.moveTo ctx x1 y1)
  (.lineTo ctx x2 y2)
  (.stroke ctx))
