(ns server.core
  (:gen-class)
  (:require [config.core :as config]
            [org.httpkit.server :as server]
            [ring.middleware.json :as rmj]
            [ring.middleware.keyword-params :as rmkp]
            [ring.middleware.params :as rmp]
            [routes.core :as routes]
            [server.state :as state]))

(defn start-server!
  "Initialize server"
  [& {:keys [address port]}]
  (let [address (or address (config/address))
        port (parse-long (or port (config/port)))]
    (println (str "Running webserver at " address ":" port "/"))
    (reset!
     state/server
     (server/run-server
      (-> #'routes/app-routes
          rmkp/wrap-keyword-params
          rmp/wrap-params
          (rmj/wrap-json-body {:keywords? true})
          rmj/wrap-json-response)
      {:legacy-return-value? false
       :ip                   address
       :port                 port}))))

(defn stop-server!
  "Halt server"
  ([]
   (when @state/server
     (and (stop-server! @state/server)
          (reset! state/server nil))))
  ([server]
   (println "Stopping webserver")
   (server/server-stop! server)))

(defn reset-server!
  "Reset server"
  [& args]
  (println "Resetting webserver")
  (stop-server!)
  (start-server! args))

(comment
  (require '[org.httpkit.client :as client])

  ;; starting server
  (start-server!)

  #_:clj-kondo/ignore
  @(client/get "http://127.0.0.1:8080/health")
  ;; =>
  {:opts   {:method :get, :url "http://127.0.0.1:8080/health"}
   :body   ""
   :headers
   {:content-length "0"
    :content-type   "application/json"
    :date           "Sun, 2 Feb 2025 04:56:05 GMT"
    :server         "http-kit"}
   :status 200}

  ;; check status
  (state/server-info @state/server)
  ;; =>
  {:is-alive? true
   :port      8080
   :status    :running}

  ;; resetting server
  (reset-server!))
