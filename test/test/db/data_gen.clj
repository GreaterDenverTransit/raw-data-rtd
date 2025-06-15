(ns test.db.data-gen
  "Namespace for generating datapotato records for malli specs"
  (:require [db.spec.combined-ridership-data :as spec.combined-ridership-data]
            [db.spec.stops :as spec.stops]
            [donut.datapotato.core :as dc]
            [donut.datapotato.next-jdbc :as dnj]
            [matcher-combinators.test]
            [malli.generator :as mg]
            [medley.core :as me]
            [next.jdbc :as jdbc]
            [next.jdbc.sql :as sql]
            [utils :refer [->kebab-case-keyword ->snake_case_symbol]]))

(defn insert!
  "inserts a single record in the next.jdbc db for an ent"
  [{{:keys [connection dbspec dbtype]} dc/fixtures-visit-key
    :as                                potato-db}
   {:keys [ent-name ent-type visit-val generate] :as _record}]
  (let [get-inserted' (or (get-in potato-db [dc/fixtures-visit-key :get-inserted])
                          dnj/get-inserted)
        table-name    (get-in (dc/ent-schema potato-db ent-name)
                              [dc/fixtures-visit-key :table-name])]

    (when-not connection
      (throw (ex-info "connection required" {})))

    (when-not table-name
      (throw (ex-info
              (format "No table name provided. Add under [:schema %s :fixtures :table-name]"
                      ent-type)
              {:ent-name ent-name
               :ent-type ent-type})))

    (let [insert-result (sql/insert! connection
                                     table-name
                                     (or generate visit-val)
                                     {:column-fn ->snake_case_symbol})]
      (me/map-keys
       ->kebab-case-keyword
       (get-inserted' {:dbspec        dbspec
                       :dbtype        dbtype
                       :connection    connection
                       :table-name    table-name
                       :insert-result insert-result})))))

(def schema
  {:combined-ridership-data
   {:fixtures  {:table-name "combined_ridership_data"}
    :generate  {:schema spec.combined-ridership-data/combined-ridership-data}
    :prefix    :crd
    :relations {:stop-id [:stops :stop-id]}}

   :stops
   {:fixtures  {:table-name "stops"}
    :generate  {:schema spec.stops/stops}
    :prefix    :s}})

;; TODO: Move these to migrations
(def table-insertions
  ["CREATE TABLE IF NOT EXISTS combined_ridership_data (
	   schedule_name	  TEXT COLLATE NOCASE,
	   route	          TEXT COLLATE NOCASE,
	   branch	        TEXT COLLATE NOCASE,
	   service_type	  TEXT COLLATE NOCASE,
	   service_mode	  TEXT COLLATE NOCASE,
	   direction_number INTEGER,
	   direction_name	TEXT COLLATE NOCASE,
	   trip_id	        INTEGER,
	   time_period	    TEXT COLLATE NOCASE,
	   sort_order	    INTEGER,
	   stop_id	        INTEGER NOT NULL,
	   boardings	      INTEGER,
	   alightings	    INTEGER,
	   load	          INTEGER,
	   schedule_year	  integer,
	   schedule_month   integer,
     schedule_days,
     service_id,
     FOREIGN KEY(stop_id) REFERENCES stops(stop_id))"
   "CREATE TABLE IF NOT EXISTS stops (
	   stop_id 	         INTEGER,
     stop_code	         INTEGER,
	   stop_name	         TEXT,
	   stop_desc	         TEXT,
	   stop_lat	         REAL,
	   stop_lon	         REAL,
	   zone_id 	         TEXT,
	   stop_url	         TEXT,
	   location_type	     INTEGER,
     parent_station	   TEXT,
	   stop_timezone	     TEXT,
	   wheelchair_boarding TEXT,
	   PRIMARY KEY(\"stop_id\"))"])

(defn drop-tables!
  [conn]
  (doseq [table ["combined_ridership_data" "stops"]]
    (jdbc/execute! conn [(str "DROP TABLE IF EXISTS " table)])))

(defn create-tables!
  [conn]
  (prn "dropping tables!")
  (drop-tables! conn)
  (doseq [table-insertion table-insertions]
    (prn "inserting table" table-insertion)
    (jdbc/execute! conn [table-insertion])))

(def potato-db
  {:schema   schema
   :generate {:generator mg/generate}
   :fixtures (merge dnj/config
                    {:dbspec {:dbtype         "sqlite"
                              :connection-uri "jdbc:sqlite::memory:"}
                     :insert insert!
                     :setup  (fn [_]
                               (prn "creating tables!")
                               (create-tables! dc/*connection*))})})
