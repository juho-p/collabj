(ns collabj.boards
  (:use org.httpkit.server))

(def clients (atom {}))

(defn add-client
  [req]
  (with-channel req con
    (swap! clients assoc con true)
    (println con " connected")
    (on-receive con
                (fn [data]
                  (doseq [client @clients]
                    (send! (key client) data false))))
    (on-close con (fn [status]
                    (swap! clients dissoc con)
                    (println con " disconnected. status: " status)))))
