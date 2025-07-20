(ns sqlite-to-postgres
  (:require [clojure.java.io :as io]
            [clojure.java.shell :as sh]
            [clojure.string :as str]
            [clojure.tools.cli :refer [parse-opts]]
            [db.core :as db]
            [next.jdbc.connection :as jdbc-conn]
            [utils :refer [partial-right prn-err]]))

(def cli-options
  [["-i" "--in IN" "Input sqlite DB file"
    :id :in
    :required "Input SQLite DB file"
    :validate [(partial-right str/ends-with? ".db")]]
   ["-o" "--out OUT" "SQL Dump file"
    :default  "dump.sql"
    :required "Output SQLite DB dump file"
    :id :out
    :validate [(partial-right str/ends-with? ".sql")]]
   ["-c" "--conn-str CONN-STRING" "PostgreSQL connection string"
    :required "PostgreSQL connection string"
    :id :conn
    :validate [(partial-right str/starts-with? "postgresql://")]]
   ["-s"
    "--skip-migration"
    "Pass to skip running migrations on PostgreSQL DB"
    :id :skip-migration?]
   ["-v" "--verbose" "Prints out steps as executed"
    :id :verbose?]])

(defn usage
  [options-summary]
  (->> ["Usage: sqlite-to-postgres [options] action"
        ""
        "Options:"
        options-summary]
       (str/join \newline)))

(defn error-msg [errors]
  (str "The following errors occurred while parsing your command:\n\n"
       (str/join \newline errors)))

(def rename-mapping
  "Mapping of dumped table names to correct table names"
  {"\"Combined_Ridership_Data\"" "combined_ridership_data"})

(defn- int->bool
  "Translates `x` into it's equivalent SQL boolean string"
  [x]
  (case (str x)
    "0" "false"
    "1" "true"
    "NULL"))

(def retype-mapping
  "Mapping of dumped table columns to type conversion"
  {"stops" {"wheelchair_boarding" {:idx 11
                                   :fn  int->bool}}
   "trips" {"direction_id" {:idx 4
                            :fn  int->bool}}})

(defn validate-args
  "Validate command line arguments. Either return a map indicating the program
  should exit (with an error message, and optional ok status), or a map
  indicating the action the program should take and the options provided."
  [args]
  (let [{:keys [options errors summary]} (parse-opts args cli-options)]
    (cond
      (:help options)
      {:exit-msg (usage summary) :ok? true}

      errors
      {:exit-msg (error-msg errors) :ok? false}

      :else
      {:options options})))

(defn exit
  [status msg]
  (prn-err "error discovered")
  (prn-err msg)
  (System/exit status))

(def table-insert-order
  "Necessary as sqlite3 does not dump tables in order based on foreign keys"
  ["shapes"
   "stops"
   "trips"
   "calendar"
   "calendar_dates"
   "stop_times"
   "combined_ridership_data"])

(defn dump!
  "Dump slite3 data from `in` to `out`"
  [{:keys [in out verbose?]}]
  (let [cmd (format "sqlite3 %s \".output %s\" \".dump --data-only --nosys\"" in out)]
    (when verbose?
      (println "Dumping sqlite DB data via " cmd))
    (sh/sh "bash" "-c" cmd)))

(defn migrate!
  "Run migrations on PostgreSQL DB at connection string `conn`
  (unless `skip-migrate?` is true)"
  [{:keys [conn skip-migrate? verbose?]}]
  (if-not skip-migrate?
    (binding [db/*db* (jdbc-conn/jdbc-url conn)]
      (when verbose?
        (println "Running PostgreSQL migrations on provided connection string")
        (db/reset!)))
    (when verbose? (println "Skipping migration due to -s/--skip-migration param"))))

(defn rename!
  "Renames DB table names and columns to follow consistent casing"
  [{:keys [out verbose?]}]
  (doseq [[from to] rename-mapping]
    (when verbose?
      (println "Renaming table " from " to " to))
    (let [arg (format "/%s/%s" from to)]
      (sh/sh "sed" arg out))))

(defn- parse-str
  [row idx])

(defn- parse-non-str
  [row idx])

(defn- parse-column
  "Consume one column of VALUES(col0, ..., coln), returning the column and the
  index of the comma terminating the column, respective to the whole row"
  [row idx]
  ;; Can't just consume each comma since there may be commas in strings
  (let [str? (= \' (nth row idx))]
    (if str?
      (parse-str row idx)
      (parse-not-str row idx))))

(defn- line?
  "True iff `line` conforms to expected shape"
  [line]
  (boolean (re-matches #"INSERT INTO \w .*" line))
  (boolean (re-matches #"INSERT INTO \W VALUES(.*);" line))
  #_(and (str/starts-with? line "INSERT INTO")
       (str/includes? line "VALUES(")
       (str/ends-with? line ");")))

(defn- coerce-values-column
  "Updates column `idx` of `line` with `f`. `line-cnt` used to ensure correctness
  with default parsing function (i.e. if # of parsed values for `line` does not
  equal `line-cnt` then the parsing function or something else is wrong)"
  [line line-cnt idx f]
  ())

(defn format!
  "Adjusts DB table column values to match type changes (e.g. 0/1 -> false/true)"
  [{:keys [out verbose?]}])

(defn import!
  "Imports data from `out` into `conn`"
  [{:keys [conn out verbose?]}]
  (when verbose?
    (println "Importing data from " out))
  (with-open [rdr (io/reader out)]
    (let [stmt-batches (partition-all 1000 (line-seq rdr))]
      (doseq [batch stmt-batches]
        (db/batch-execute! conn batch)))))

(defn cleanup!
  "Deletes `out` file"
  [{:keys [out verbose?]}]
  (when verbose?
    (println "Deleting " out))
  (sh/sh "rm" out))

(defn -main
  [& args]
  (let [{:keys [exit-msg ok? options]} (parse-opts args cli-options)]
    (if exit-msg
      (exit (if ok? 0 1) exit-msg)
      (doto options
        ;; TODO uncomment
        #_dump!
        migrate!
        rename!
        import!
        #_cleanup!))))
