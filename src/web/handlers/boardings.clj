(ns web.handlers.boardings
  (:require [config.core :as config]
            [domain.boardings :as boardings]
            [rop.core :as rop]
            [web.error :as err]
            [web.status :as status]))

(defn resp
  [req]
  (if (err/err? req)
    (err/std-err req)
    (let [boardings (get-in req [:ctx :boardings])]
      {:status  status/okay
       :headers {"Content-Type" "application/json"}
       :body    boardings})))

(defn =boardings=
  [{:keys [params] :as req}]
  (let [count' (or (:count params) (config/default-count))
        order (or (:order params) :desc)
        start-date (:start-date params)
        end-date (:end-date params)
        db (:db req)
        boardings (boardings/boardings db
                                       {:count'     count'
                                        :end-date   end-date
                                        :order      order
                                        :start-date start-date})]
    (if boardings
      (rop/succeed (assoc-in req [:ctx :boardings] boardings))
      (rop/fail (assoc req
                       :error
                       (ex-info "Boardings could not be returned"
                                {:status status/internal-server-error}))))))

;; TODO: Add support for 400 error handling
(defn handler
  [req]
  ;; TODO: Add context map interceptor
  (resp (rop/>>= (assoc req :ctx {})
                 =boardings=)))
