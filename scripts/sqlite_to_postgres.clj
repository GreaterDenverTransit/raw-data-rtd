(ns sqlite-to-postgres
  (:require [clojure.java.shell :as sh]
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
        (println "Running PostgreSQL migrations on provided connection string"))
      )
    (when verbose? (println "Skipping migration due to -s/--skip-migration param"))))

(defn rename!
  [opts])

(defn import!
  [opts])

(defn cleanup!
  [opts])

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
        cleanup!))))
