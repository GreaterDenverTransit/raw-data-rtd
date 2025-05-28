(ns test.db.data-gen-test
  (:require [clojure.test :refer [deftest is testing use-fixtures]]
            [donut.datapotato.core :as dc]
            [next.jdbc :as jdbc]
            [test.helper :as helper]))

(use-fixtures :each helper/system-fixture)

(deftest potato-db-test
  (testing "potato-db fixture works"
    (let [db (dc/insert-fixtures {:combined-ridership-data [[1]]})]
      (is (= 1 (count (jdbc/execute! dc/*connection*
                                     ["SELECT * from \"Combined_Ridership_Data\""]))))
      (is (some? db)))))
