(ns db.core
  (:require [config.core :as config]
            [honey.sql :as sql]
            [medley.core :as m]
            [next.jdbc :as jdbc]
            [next.jdbc.connection :as jdbc-conn]
            [next.jdbc.result-set :as rs]
            [utils :refer [->kebab-case-keyword]])
  (:import [org.flywaydb.core Flyway]))

;; TODO: Add connection pools
(def ^:dynamic *db* (jdbc/get-datasource (config/db)))

(def ^:dynamic *db-url* (jdbc-conn/jdbc-url (config/db)))

(def flyway
  (-> (Flyway/configure)
      (.locations (into-array String ["classpath:db/migrations"]))
      (.dataSource *db-url* nil nil)
      (.cleanDisabled false)
      (.load)))

(defn migrate! [] (.migrate flyway))

(defn clean! [] (.clean flyway))

(defn reset! [] (clean!) (migrate!))

(defn execute!
  [db hsql]
  (mapv
   (partial m/map-keys ->kebab-case-keyword)
   (jdbc/execute!
    (jdbc/with-options db {:builder-fn rs/as-unqualified-kebab-maps})
    (sql/format hsql))))

(defn batch-execute!
  "Takes a collection of SQL strings (without semicolons) `sql-strs` and commits
  them all transactionally to `db` in a single statement"
  [db sql-strs]
  (jdbc/with-transaction [tx db]
    (let [conn (jdbc/get-connection tx)
          _ (.setAutoCommit conn false)
          stmt (.createStatement conn)]
      (doseq [sql-str sql-strs]
        (.addBatch stmt sql-str))
      (.executeBatch stmt)
      (.commit conn)
      (.close conn))))

(comment
  (execute! *db* {:select [:*] :from [:calendar] :limit 1})
  ;=>
  '({:weeksinrange 20.0
     :nettotaldays 152.0
     :addeddays    96
     :endweekday   "6"
     :startweekday "0"
     :saturday     0
     :tuesday      1
     :wednesday    1
     :end_date     20140104
     :start_date   20130818
     :sunday       0
     :friday       1
     :excepteddays 44
     :monday       1
     :service_id   "2013_08_WK"
     :thursday     1
     :daysinrange  140.0}))
