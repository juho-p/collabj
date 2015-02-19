(ns collabj.dom)

(def window js/window)
(def doc (.-document window))

(defn by-id [id] (.getElementById doc id))

(defn listen [elem eventname fun]
  (.addEventListener elem eventname fun))
