(ns server.state
  "Namespace for housing the server state"
  (:require [clojure.string :as str]))

(defonce server (atom nil))

(defn server-info
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
