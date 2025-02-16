(ns routes.core
  (:require [compojure.core :refer [defroutes GET POST]]
            [compojure.route :as route]
            [web.handlers.healthcheck :as healthcheck]))

(defroutes app-routes
  (GET "/health" [] healthcheck/handler)
  #_(POST "/boardings" )
  (route/not-found "Route not found"))
