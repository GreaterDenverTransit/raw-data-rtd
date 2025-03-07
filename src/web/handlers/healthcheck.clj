(ns web.handlers.healthcheck
  (:require [server.state :as state]
            [web.status :as status]))

(defn healthy?
  [{:keys [is-alive? port status]}]
  (and is-alive?
       (number? port)
       (pos? port)
       (= :running status)))

(defn handler
  [_args]
  (if (healthy? (state/server-info @state/server))
    {:status  status/okay
     :headers {"Content-Type" "application/json"}
     :body    {}}
    {:status  status/service-unavailable
     :headers {"Content-Type" "application/json"}
     :body    {}}))
