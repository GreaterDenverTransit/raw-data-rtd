(ns web.handlers.healthcheck)

(defn handler
  [_req]
  {:status  200
   :headers {"Content-Type" "text/json"}
   :body    {}})
