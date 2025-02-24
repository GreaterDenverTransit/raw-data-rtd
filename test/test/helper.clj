(ns test.helper
  (:require [test.config :as config]
            [next.jdbc :as jdbc]
            [jsonista.core :as json]
            [ring.mock.request :as mock]))

(def ^:dynamic test-db nil)

(defn- inject-test-db
  [req]
  (assoc req :db test-db))

(defn- body->params
  [req]
  (prn "body" (:body req))
  (cond-> req
    (:body req) (->
                 (assoc :params (json/read-value (slurp (:body req)) json/keyword-keys-object-mapper)))))

(defn mock-json-req
  [{:keys [body handler method url]}]
  (prn "body" body)
  (prn "handler" handler)
  (prn "method" method)
  (prn "url" url)
  (-> (mock/request method url)
      (mock/json-body body)
      body->params
      inject-test-db
      handler))

;; NOTE: Since the db is read-only it *should* be fine just to load up the actual
;; db, if this changes then building a separate DB will be necessary
(defn system-fixture
  "Default test fixture for setting up a db for tests"
  [f]
  (binding [test-db (jdbc/get-datasource (config/db))]
    (f)))
