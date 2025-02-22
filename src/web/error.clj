(ns web.error
  (:require [web.status :as status]))

(defn err?
  "True iff `req` contains an error state"
  [req]
  (some? (:error req)))

(defn std-err
  "Maps `error` to generic error response"
  [{:keys [error] :as _request}]
  (some-> {:status  (or (-> error ex-data :status) status/internal-server-error)
           :headers {"Content-type" "text/json"}}
          (-> error ex-data :body) (assoc :body (-> error ex-data :body))))
