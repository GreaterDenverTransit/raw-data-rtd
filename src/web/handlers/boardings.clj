(ns web.handlers.boardings
  (:require [config.core :as config]
            [domain.boardings :as boardings]
            [rop.core :as rop]
            [web.status :as status]))

(defn resp
  [req]
  {:status  status/okay
   :headers {"Content-Type" "text/json"}
   :body    {:body (clojure.edn/read-string (slurp body))
             #_#_:request request}})

(defn =boardings=
  [{:keys [body] :as req}]
  (let [count' (or (:count body) (config/default-count))
        order (or (:order body) :desc)
        start-date (:start-date body)
        end-date (:end-date body)
        boardings (boardings/boardings {:count'     count'
                                        :end-date   end-date
                                        :order      order
                                        :start-date start-date})]
    (if boardings
      (rop/succeed (assoc req )))))

;; TODO: Add context map interceptor
(defn handler
  [req]
  (rop/>>= (assoc req :ctx {})
           =boardings=))
