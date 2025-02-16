(ns server.core
  (:gen-class)
  (:require [clojure.string :as str]
            [config.core :as config]
            [org.httpkit.server :as server]
            [ring.middleware.keyword-params :as rmkp]
            [ring.middleware.params :as rmp]
            [routes.core :as routes]))

(defonce server (atom nil))

(defn start-server!
  "Initialize server"
  [& {:keys [address port]}]
  (let [address (or address (config/address))
        port (parse-long (or port (config/port)))]
    (println (str "Running webserver at " address ":" port "/"))
    (reset!
     server
     (server/run-server
      (-> #'routes/app-routes rmkp/wrap-keyword-params rmp/wrap-params)
      {:legacy-return-value? false
       :ip                   address
       :port                 port}))))

(defn stop-server!
  "Halt server"
  ([]
   (when @server
     (prn @server)
     (and (stop-server! @server)
          (reset! server nil))))
  ([server]
   (println "Stopping webserver")
   (server/server-stop! server)))

(defn reset-server!
  "Reset server"
  [& args]
  (println "Resetting webserver")
  (stop-server!)
  (start-server! args))

(defn- server-info
  "Debug fn for displaying basic data about `server`"
  ([]
   (server-info @server))
  ([server]
   (if (instance? org.httpkit.server.HttpServer server)
     {:is-alive? (.isAlive ^org.httpkit.server.HttpServer server)
      :port      (.getPort ^org.httpkit.server.HttpServer server)
      :status    (-> (.getStatus ^org.httpkit.server.HttpServer server) str/lower-case keyword)}
     {:is-alive? false
      :port      nil
      :status    nil})))

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
    :content-type   "text/json"
    :date           "Sun, 2 Feb 2025 04:56:05 GMT"
    :server         "http-kit"}
   :status 200}

  ;; check status
  (server-info @server)
  ;; =>
  {:is-alive? true
   :port      8080
   :status    :running}

  ;; resetting server
  (reset-server!))
