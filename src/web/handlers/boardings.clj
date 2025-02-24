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
       :body    {:body boardings}})))

;; TODO: DELETE
(def body-atom (atom nil))
(def req-atom (atom nil))
(def boardings-arg-atom (atom nil))

(defn =boardings=
  [{:keys [params] :as req}]
  (let [count' (or (:count params) (config/default-count))
        order (or (:order params) :desc)
        start-date (:start-date params)
        end-date (:end-date params)
        _ (reset! req-atom req)
        _ (reset! body-atom params)
        _ (reset! boardings-arg-atom {:count'     count'
                                      :end-date   end-date
                                      :order      order
                                      :start-date start-date})
        boardings (boardings/boardings {:count'     count'
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
  #p req
  ;; TODO: Add context map interceptor
  (resp (rop/>>= (assoc req :ctx {})
                 =boardings=)))
