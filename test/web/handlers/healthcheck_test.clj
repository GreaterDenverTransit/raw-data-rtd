(ns web.handlers.healthcheck-test
  (:require [web.handlers.healthcheck :as sut]
            [clojure.test :refer [deftest is testing]]
            [ring.mock.request :as mock]))


(deftest healthcheck-test
  (testing "Healthcheck endpoint works"
    (is (= (sut/handler (mock/request :get "/health"))
           {:status 200
            :headers {"Content-Type" "text/json"}
            :body    {}}))))
