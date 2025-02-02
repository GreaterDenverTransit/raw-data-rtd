(ns routes.core
  (:require [compojure.core :refer [defroutes GET]]
            [compojure.route :as route]
            [web.handlers.healthcheck :as healthcheck]))

(defroutes app-routes
  (GET "/health" [] healthcheck/handler)
  (route/not-found "Route not found"))
