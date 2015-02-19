(ns collabj.core
  (:use [compojure.core :only (defroutes GET)]
        ring.util.response
        ring.middleware.cors
        org.httpkit.server)
  (:require [compojure.route :as route]
            [compojure.handler :as handler]
            [ring.middleware.reload :as reload]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [collabj.views :as views]))

(defn index
  [req]
  (views/index))

(def clients (atom {}))

(defn add-client
  [req]
  (with-channel req con
    (swap! clients assoc con true)
    (println con " connected")
    (on-close con (fn [status]
                    (swap! clients dissoc con)
                    (println con " disconnected. status: " status)))))

(future (loop []
          (doseq [client @clients]
            (send! (key client) "Hello\n" false))
          (Thread/sleep 5000)
          (recur)))

(defroutes app
  (GET "/ws" [] add-client)
  (GET "/" [] index))

(def application (-> (handler/site app)
                     reload/wrap-reload))

(defn -main [& args]
  (let [port (Integer/parseInt 
               (or (System/getenv "PORT") "8080"))]
    (run-server application {:port port :join? false})))
