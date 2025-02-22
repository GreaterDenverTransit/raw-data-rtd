(ns db.boardings
  (:require [db.core :as db
             honeysql.core :as sql]))

(defn select
  [{:keys [limit where]}]
  (let [query {:select [:*]
               :from   [[]]}]
    (db/select
     (some-> query
             limit (assoc :limit limit)
             where (assoc :where where)))))
