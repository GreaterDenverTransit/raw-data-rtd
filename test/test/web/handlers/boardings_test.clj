(ns test.web.handlers.boardings-test
  (:require [clojure.test :refer [deftest is testing use-fixtures]]
            [donut.datapotato.core :as dc]
            [test.db.data-gen :as gen]
            [test.helper :as helper]
            [web.handlers.boardings :as sut]
            [next.jdbc :as jdbc]))

#_(use-fixtures :each helper/system-fixture)

(deftest top-boardings-test
  (testing "Boardings endpoint can return top boardings"
    (try
    (dc/generate
     gen/potato-db
     {:Combined-Ridership-Data [{:set {:schedule-name  "Apr19 (Friday)"
                                       :route          "A"
                                       :direction-name "E-Bound"
                                       :stop-name      "Union Station Track 1"
                                       :boardings      6099.166}}
                                {:set {:schedule-name  "Apr19 (Weekday)"
                                       :route          "A"
                                       :direction-name "E-Bound"
                                       :stop-name      "Union Station Track 1"
                                       :boardings      6780.879}}
                                {:set {:schedule-name  "Apr19 (Saturday)"
                                       :route          "A"
                                       :direction-name "W-Bound"
                                       :stop-name      "Denver Airport Station"
                                       :boardings      4797.5}}
                                {:set {:schedule-name  "Apr19 (Friday)"
                                       :route          "A"
                                       :direction-name "W-Bound"
                                       :stop-name      "Denver Airport Station"
                                       :boardings      6755.833}}
                                {:set {:schedule-name  "Apr19 (Weekday)"
                                       :route          "A"
                                       :direction-name "W-Bound"
                                       :stop-name      "Denver Airport Station"
                                       :boardings      6457.426}}
                                ;; B line data here is totally fake
                                {:set {:schedule-name  "Apr 19 (Weekday)"
                                       :route          "B"
                                       :direction-name "W-Bound"
                                       :stop-name      "Westminster Station"
                                       :boardings      913.5}}]})
    (catch Exception e (prn e)))
    (is true)
    (is (= (helper/mock-json-req {:body    {:count      5
                                              :start-date "Apr19 (Weekday)"
                                              :end-date   "Apr19 (Weekday)"}
                                    :handler sut/handler
                                    :method  :post
                                    :url     "/boardings"})
             {:status  200
              :headers {"Content-Type" "application/json"}
              :body    [{:schedule-name  "Apr19 (Weekday)"
                         :route          "A"
                         :direction-name "E-Bound"
                         :stop-name      "Union Station Track 1"
                         :boardings      6780.879}
                        {:schedule-name  "Apr19 (Friday)"
                         :route          "A"
                         :direction-name "W-Bound"
                         :stop-name      "Denver Airport Station"
                         :boardings      6755.833}
                        {:schedule-name  "Apr19 (Weekday)"
                         :route          "A"
                         :direction-name "W-Bound"
                         :stop-name      "Denver Airport Station"
                         :boardings      6457.426}
                        {:schedule-name  "Apr19 (Friday)"
                         :route          "A"
                         :direction-name "E-Bound"
                         :stop-name      "Union Station Track 1"
                         :boardings      6099.166}
                        {:schedule-name  "Apr19 (Saturday)"
                         :route          "A"
                         :direction-name "W-Bound"
                         :stop-name      "Denver Airport Station"
                         :boardings      4797.5}]}))))

#_(deftest bottom-boardings-test
  (testing "Boardings endpoint can return bottom boardings"
    (is (= (helper/mock-json-req {:body    {:count      5
                                            :order      :asc
                                            :start-date "Apr19 (Weekday)"
                                            :end-date   "Apr19 (Weekday)"}
                                  :handler sut/handler
                                  :method  :post
                                  :url     "/boardings"})
           {:status  200
            :headers {"Content-Type" "application/json"}
            :body    [{:schedule-name  "Apr19 (Friday)"
                       :route          103
                       :direction-name "W-Bound"
                       :stop-name      "Jeffco Government Center Station"
                       :boardings      0}
                      {:schedule-name  "Apr19 (Friday)"
                       :route          113
                       :direction-name "E-Bound"
                       :stop-name      "Union Station Track 8"
                       :boardings      0}
                      {:schedule-name  "Apr19 (Saturday)"
                       :route          0
                       :direction-name "N-Bound"
                       :stop-name      "S Broadway & S Bannock St"
                       :boardings      0}
                      {:schedule-name  "Apr19 (Saturday)"
                       :route          0
                       :direction-name "N-Bound"
                       :stop-name      "S Broadway & W Fremont Pl"
                       :boardings      0}
                      {:schedule-name  "Apr19 (Saturday)"
                       :route          0
                       :direction-name "N-Bound"
                       :stop-name      "8700 Block Ridgeline Blvd"
                       :boardings      0}]}))))
