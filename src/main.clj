(ns main
  (:gen-class)
  (:require [compojure.core :refer [defroutes GET POST ANY]]
            [compojure.route :as route]
            [org.httpkit.server :as server]))

(defn fps-handler [_req]
  {:status  200
   :headers {"Content-Type" "text/html"}
   :body    "Pew pew!"})

(defn mail-man []
  "{\"Spongebob Narrator\": \"5 years later...\"}")

(defn mail-handler [_req]
  {:status  200
   :headers {"Content-Type" "text/json"}
   :body    (mail-man)})

(defn general-handler [_req]
  {:status  200
   :headers {"Content-Type" "text/html"}
   :body    "All hail General Zod!"})

(defroutes app-routes
  (GET "/" [] fps-handler)
  (POST "/postoffice" [] mail-handler)
  (ANY "/anything-goes" [] general-handler)
  (route/not-found "You Must Be New Here"))

(defn -main
  "This is our app's entry point"
  [& _args]
  (let [port (Integer/parseInt (or (System/getenv "PORT") "8080"))]
  (server/run-server #'app-routes {:port port})
  (println (str "Running webserver at http://127.0.0.1:" port "/"))))

(comment
  (require '[org.httpkit.client :as client])

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
