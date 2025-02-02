(ns server.core
  (:gen-class)
  (:require [org.httpkit.server :as server]
            [routes.core :as routes]))

(defn start
  "Initialize server"
  [& _args]
  (let [port (parse-long (or (System/getenv "PORT") "8080"))]
    (server/run-server #'routes/app-routes {:port port})
    (println (str "Running webserver at http://127.0.0.1:" port "/"))))

(comment
  (require '[org.httpkit.client :as client])

  (start)

  @(client/get "http://127.0.0.1:8080/")
  ;; => {:opts {:method :get, :url "http://127.0.0.1:8080/"},
  ;;     :body "Pew pew!",
  ;;     :headers
  ;;     {:content-length "8",
  ;;      :content-type "text/html",
  ;;      :date "Sun, 2 Feb 2025 04:13:36 GMT",
  ;;      :server "http-kit"},
  ;;     :status 200}
)
