(ns db.boardings
  (:require [db.core :as db]))

(defn select
  [db {:keys [limit
              order-by
              where]}]
  (let [query {:select   [:total.schedule-name
                          :total.route
                          :total.direction-name
                          :s.stop-name
                          [[:sum :boardings] :boardings]]
               :from     [[:combined_ridership_data :total]]
               :join     [[:stops :s]
                          [:= :s.stop-id :total.stop-id]]
               :group-by [:total.schedule-name
                          :total.route
                          :total.direction-name
                          :total.stop-id]}]

    (db/execute!
     db
     (cond-> query
       limit    (assoc :limit limit)
       order-by (assoc :order-by order-by)
       where    (assoc :where where)))))

(defn top-n
  [db n start-date end-date]
  (select db {:limit    n
              :order-by [[[:sum :boardings] :desc]]
              :where    [:or
                         [:= [:substr :schedule-name 1 5] start-date]
                         [:= [:substr :schedule-name 1 5] end-date]]}))

(defn bottom-n
  [db n start-date end-date]
  (select db {:limit    n
              :order-by [[[:sum :boardings] :asc]]
              :where    [:or
                         [:= [:substr :schedule-name 1 5] start-date]
                         [:= [:substr :schedule-name 1 5] end-date]]}))
