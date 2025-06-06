(ns test.web.handlers.boardings-test
  (:require [clojure.test :refer [deftest is testing use-fixtures]]
            [donut.datapotato.core :as dc]
            [test.helper :as helper]
            [web.handlers.boardings :as sut]))

(use-fixtures :each helper/system-fixture)

(deftest top-boardings-test
  (testing "Boardings endpoint can return top boardings"
    (dc/insert-fixtures
     {:combined-ridership-data
      [{:refs {:stop-id :union}
        :set  {:schedule-name  "Apr19 (Friday)"
               :route          "A"
               :direction-name "E-Bound"
               :boardings      6099.166}}
       {:refs {:stop-id :union}
        :set  {:schedule-name  "Apr19 (Weekday)"
               :route          "A"
               :direction-name "E-Bound"
               :boardings      6780.879}}
       {:refs {:stop-id :den}
        :set  {:schedule-name  "Apr19 (Saturday)"
               :route          "A"
               :direction-name "W-Bound"
               :boardings      4797.5}}
       {:refs {:stop-id :den}
        :set  {:schedule-name  "Apr19 (Friday)"
               :route          "A"
               :direction-name "W-Bound"
               :boardings      6755.833}}
       {:refs {:stop-id :den}
        :set  {:schedule-name  "Apr19 (Weekday)"
               :route          "A"
               :direction-name "W-Bound"
               :boardings      6457.426}}
       ;; B line data here is totally fake
       {:refs {:stop-id :westminster}
        :set  {:schedule-name  "Apr 19 (Weekday)"
               :route          "B"
               :direction-name "W-Bound"
               :boardings      913.5}}]

      :stops
      [{:ent-name :union
        :set      {:stop-name "Union Station Track 1"}}
       {:ent-name :den
        :set      {:stop-name "Denver Airport Station"}}
       {:ent-name :westminster
        :set      {:stop-name "Westminster Station"}}]})
    (let [{:keys [status body]} (helper/mock-json-req
                                 {:body    {:count      5
                                            :start-date "Apr19 (Weekday)"
                                            :end-date   "Apr19 (Weekday)"}
                                  :handler sut/handler
                                  :method  :post
                                  :url     "/boardings"})]

      (is (= 200 status))
      (is (= [{:schedule-name  "Apr19 (Weekday)"
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
               :boardings      4797.5}]
           body)))))

(deftest bottom-boardings-test
  (testing "Boardings endpoint can return bottom boardings"
    (dc/insert-fixtures
     {:combined-ridership-data
      [{:refs {:stop-id :union}
        :set  {:schedule-name  "Apr19 (Friday)"
               :route          113
               :direction-name "E-Bound"
               :boardings      1}}
       {:refs {:stop-id :jeffco}
        :set  {:schedule-name  "Apr19 (Friday)"
               :route          103
               :direction-name "W-Bound"
               :boardings      0}}
       {:refs {:stop-id :ridgeline}
        :set  {:schedule-name  "Apr19 (Saturday)"
               :route          0
               :direction-name "N-Bound"
               :boardings      3.7}}
       {:refs {:stop-id :broadway+bannock}
        :set  {:schedule-name  "Apr19 (Saturday)"
               :route          0
               :direction-name "N-Bound"
               :boardings      2.7}}
       {:refs {:stop-id :broadway+fremont}
        :set  {:schedule-name  "Apr19 (Saturday)"
               :route          0
               :direction-name "N-Bound"
               :boardings      3.5}}
       ;; B line data here is totally fake
       {:refs {:stop-id :westminster}
        :set  {:schedule-name  "Apr 19 (Weekday)"
               :route          "B"
               :direction-name "W-Bound"
               :boardings      913.5}}]

      :stops
      [{:ent-name :jeffco
        :set      {:stop-name "Jeffco Government Center Station"}}
       {:ent-name :union
        :set      {:stop-name "Union Station Track 8"}}
       {:ent-name :broadway+fremont
        :set      {:stop-name "S Broadway & W Fremont Pl"}}
       {:ent-name :broadway+bannock
        :set      {:stop-name "S Broadway & S Bannock St"}}
       {:ent-name :ridgeline
        :set      {:stop-name "8700 Block Ridgeline Blvd"}}
       {:ent-name :westminster
        :set      {:stop-name "Westminster Station"}}]})
    (is (= {:status  200
            :headers {"Content-Type" "application/json"}
            :body    [{:schedule-name  "Apr19 (Friday)"
                       :route          "103"
                       :direction-name "W-Bound"
                       :stop-name      "Jeffco Government Center Station"
                       :boardings      0}
                      {:schedule-name  "Apr19 (Friday)"
                       :route          "113"
                       :direction-name "E-Bound"
                       :stop-name      "Union Station Track 8"
                       :boardings      1}
                      {:schedule-name  "Apr19 (Saturday)"
                       :route          "0"
                       :direction-name "N-Bound"
                       :stop-name      "S Broadway & S Bannock St"
                       :boardings      2.7}
                      {:schedule-name  "Apr19 (Saturday)"
                       :route          "0"
                       :direction-name "N-Bound"
                       :stop-name      "S Broadway & W Fremont Pl"
                       :boardings      3.5}
                      {:schedule-name  "Apr19 (Saturday)"
                       :route          "0"
                       :direction-name "N-Bound"
                       :stop-name      "8700 Block Ridgeline Blvd"
                       :boardings      3.7}]}
           (helper/mock-json-req {:body    {:count      5
                                            :order      :asc
                                            :start-date "Apr19 (Weekday)"
                                            :end-date   "Apr19 (Weekday)"}
                                  :handler sut/handler
                                  :method  :post
                                  :url     "/boardings"})))))
