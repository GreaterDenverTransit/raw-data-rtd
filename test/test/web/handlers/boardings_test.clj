(ns test.web.handlers.boardings-test
  (:require [clojure.test :refer [deftest is testing use-fixtures]]
            [test.helper :as helper]
            [web.handlers.boardings :as sut]))

(use-fixtures :each helper/system-fixture)

(deftest ^:focus top-boardings-test
  (testing "Boardings endpoint can return top boardings"
    (prn "ayo")
    (prn "test-db" helper/test-db)
    (is (= (helper/mock-json-req {:body    {:count      5
                                            :start-date "Apr19 (Weekday)"
                                            :end-date   "Apr19 (Weekday)"}
                                  :handler sut/handler
                                  :method  :post
                                  :url     "/boardings"})
           {:status  200
            :headers {"Content-Type" "application/json"}
            :body    []}))))
