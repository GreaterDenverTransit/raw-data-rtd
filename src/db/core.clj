(ns db.core
  (:require [config.core :as config]
            [next.jdbc :as jdbc]
            [next.jdbc.result-set :as rs]
            [honey.sql :as sql]))

;; TODO: Add connection pools
(def db (jdbc/get-datasource (config/db)))

(defn execute!
  [db hsql]
  (jdbc/execute!
   (jdbc/with-options db {:builder-fn rs/as-unqualified-kebab-maps})
   (sql/format hsql)))

(comment
  (execute! db {:select [:*] :from [:calendar] :limit 1})
  ;=>
  '({:weeksinrange 20.0,
     :nettotaldays 152.0,
     :addeddays    96,
     :endweekday   "6",
     :startweekday "0",
     :saturday     0,
     :tuesday      1,
     :wednesday    1,
     :end_date     20140104,
     :start_date   20130818,
     :sunday       0,
     :friday       1,
     :excepteddays 44,
     :monday       1,
     :service_id   "2013_08_WK",
     :thursday     1,
     :daysinrange  140.0}))
