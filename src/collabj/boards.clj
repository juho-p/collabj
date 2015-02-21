(ns collabj.boards
  (:use org.httpkit.server))

(def boards (ref {}))

(defn add-client-to-board
  [board-id client]
  (dosync
    (if (not (contains? @boards board-id))
      (alter boards assoc board-id (ref {client true}))
      (alter (@boards board-id) assoc client true))))

(defn remove-client-from-board
  [board-id client]
  (dosync
    (let [new-board (dissoc @(@boards board-id) client)]
      (if (empty? new-board)
        (alter boards dissoc board-id)
        (ref-set (@boards board-id) new-board)))))

(defn add-client
  [req board-id]
  (with-channel req con
    (add-client-to-board board-id con)
    (println con " connected to " board-id)
    (on-receive con
                (fn [data]
                  (println "recv " board-id)
                  (doseq [client @(@boards board-id)]
                    (send! (key client) data false))))
    (on-close con (fn [status]
                    (remove-client-from-board board-id con)
                    (println "BOARDS:")
                    (doseq [board @boards]
                      (println board))
                    (println con " disconnected. status: " status)))))
