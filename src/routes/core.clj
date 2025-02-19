(ns routes.core
  (:require [compojure.core :refer [defroutes GET POST]]
            [compojure.route :as route]
            [web.handlers.boardings :as boardings]
            [web.handlers.healthcheck :as healthcheck]))

(defroutes app-routes
  (GET "/health" [] healthcheck/handler)
  (POST "/boardings" request (boardings/handler request))
  (route/not-found "Route not found"))
