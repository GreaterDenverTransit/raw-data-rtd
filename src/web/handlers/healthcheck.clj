(ns web.handlers.healthcheck)

(defn handler
  [_args]
  {:status  200
   :headers {"Content-Type" "text/json"}
   :body    {}})
