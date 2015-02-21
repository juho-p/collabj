(ns collabj.boards
  (:use org.httpkit.server))

(def boards (ref {}))

(defn new-board [client]
  { :clients {client true} :content [] })

(defn add-client-to-board
  [board-id client]
  (dosync
    (if (not (contains? @boards board-id))
      (alter boards assoc board-id (ref (new-board client)))
      (alter (@boards board-id) assoc-in [:clients client] true))))

(defn remove-client-from-board
  [board-id client]
  (dosync
    (let [new-clients (dissoc (:clients @(@boards board-id)) client)]
      (if (empty? (:clients new-clients))
        (alter boards dissoc board-id)
        (alter (@boards board-id) assoc :clients new-clients)))))

(defn add-content
  [board-id content]
  (dosync
    (alter (@boards board-id)
           (fn [board]
             (update-in board [:content] conj content)))))

(defn add-client
  [req board-id]
  (with-channel req con
    (add-client-to-board board-id con)
    (println con " connected to " board-id)
    (doseq [content (:content @(@boards board-id))]
      (send! con content false))
    (on-receive con
                (fn [data]
                  (add-content board-id data)
                  (println @boards)
                  (doseq [client (:clients @(@boards board-id))]
                    (send! (key client) data false))))
    (on-close con (fn [status]
                    (remove-client-from-board board-id con)
                    (println con " disconnected. status: " status)))))
