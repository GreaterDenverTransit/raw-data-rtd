(ns server.core
  (:gen-class)
  (:require [org.httpkit.server :as server]
            [routes.core :as routes]))

(defn start
  "Initialize server"
  [& _args]
  (let [port (parse-long (or (System/getenv "PORT") "8080"))]
    (println (str "Running webserver at http://127.0.0.1:" port "/"))
    (server/run-server #'routes/app-routes {:port port})))

(comment
  (require '[org.httpkit.client :as client])

  (start)

  #_:clj-kondo/ignore
  @(client/get "http://127.0.0.1:8080/health")
  ;; =>
  {:opts   {:method :get, :url "http://127.0.0.1:8080/health"}
   :body   ""
   :headers
   {:content-length "0"
    :content-type   "text/json"
    :date           "Sun, 2 Feb 2025 04:56:05 GMT"
    :server         "http-kit"}
   :status 200})
