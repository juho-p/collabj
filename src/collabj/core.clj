(ns collabj.core
  (:use [compojure.core :only (defroutes GET)]
        ring.util.response
        ring.middleware.cors
        org.httpkit.server)
  (:require [compojure.route :as route]
            [compojure.handler :as handler]
            [ring.middleware.reload :as reload]
            [collabj.views :as views]))

(defn index
  [req]
  (views/index))

(defroutes routes
  (GET "/" [] index))

(def application (-> (handler/site routes)
                     reload/wrap-reload
                     (wrap-cors
                      :access-control-allow-origin #".+")))

(defn -main [& args]
  (let [port (Integer/parseInt 
               (or (System/getenv "PORT") "8080"))]
    (run-server application {:port port :join? false})))
