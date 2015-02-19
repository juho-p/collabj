(ns collabj.core
  (:use [compojure.core :only (defroutes GET routes)]
        ring.util.response
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
    (on-receive con
                (fn [data]
                  (doseq [client @clients]
                    (send! (key client) data false))))
    (on-close con (fn [status]
                    (swap! clients dissoc con)
                    (println con " disconnected. status: " status)))))

(defroutes app-routes
  (route/resources "/")
  (GET "/ws" [] add-client)
  (GET "/" [] index)
  (route/not-found "Not found."))

(def app-handler (-> (handler/site app-routes)
                     reload/wrap-reload))

(defn app
  [req]
  (app-handler req))

(defn -main [& args]
  (let [port (Integer/parseInt 
               (or (System/getenv "PORT") "8080"))]
    (run-server app {:port port :join? false})))
