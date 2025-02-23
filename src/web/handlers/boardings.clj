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

(def body-atom (atom nil))
(def json-body-atom (atom nil))

(defn =boardings=
  [{:keys [body json-body] :as req}]
  (let [count' (or (:count body) (config/default-count))
        order (or (:order body) :desc)
        start-date (:start-date body)
        end-date (:end-date body)
        _ (reset! body-atom body)
        _ (reset! json-body-atom json-body)
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

(defn handler
  [req]
  #p req
  ;; TODO: Add context map interceptor
  (resp (rop/>>= (assoc req :ctx {})
                 =boardings=)))
