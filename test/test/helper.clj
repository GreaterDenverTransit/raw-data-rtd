(ns test.helper
  (:require [db.core :as db :refer [*db*]]
            [donut.datapotato.core :as dc]
            [jsonista.core :as json]
            [test.config :as config]
            [test.db.data-gen :as gen]
            [next.jdbc :as jdbc]
            [next.jdbc.connection :as jdbc-conn]
            [ring.mock.request :as mock]))

(def ^:dynamic test-db nil)

(defn- inject-test-db
  [req]
  (assoc req :db dc/*connection*))

(defn- body->params
  [req]
  (cond-> req
    (:body req) (assoc :params (json/read-value (slurp (:body req))
                                                json/keyword-keys-object-mapper))))

(defn mock-json-req
  [{:keys [body handler method url]}]
  (-> (mock/request method url)
      (mock/json-body body)
      body->params
      inject-test-db
      handler))

(defn system-fixture
  "Default test fixture for setting up a db for tests"
  [f]
  (dc/with-fixtures gen/potato-db (f)))

(comment
  ;; preserving this dead code temporarily in case approach changes
  (defn system-fixture'
    "Default test fixture for setting up a db for tests"
    [f]
    (binding [*db* (jdbc-conn/jdbc-url (config/db))
              test-db (jdbc/get-datasource (config/db))]
      (db/reset!)
      (f)
      (db/clean!))))
