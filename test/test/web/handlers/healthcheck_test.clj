(ns test.web.handlers.healthcheck-test
  (:require [clojure.test :refer [deftest is testing]]
            [ring.mock.request :as mock]
            [server.core :as server]
            [server.state :as state]
            [web.handlers.healthcheck :as sut]))

;; NOTE: These tests may become flaky if run in parallel due to shared server
;; atom
(deftest healthcheck-success-test
  (testing "Healthcheck endpoint returns 200 when server is healthy"
    (server/start-server!)
    (is (= {:status  200
            :headers {"Content-Type" "application/json"}
            :body    {}}
           (sut/handler (mock/request :get "/health"))))
    (server/stop-server!)))

(deftest healthcheck-failure-test
  (testing "Healthcheck endpoint returns 503 when server"
    (let [service-unavailable-resp {:status  503
                                    :headers {"Content-Type" "application/json"}
                                    :body    {}}]
    (testing "not started"
      (is (= service-unavailable-resp
             (sut/handler (mock/request :get "/health")))))
    (testing "not alive"
      (reset! state/server {:is-alive? false
                            :port      8080
                            :status    :running})
      (is (= service-unavailable-resp
             (sut/handler (mock/request :get "/health")))))
    (testing "missing port"
      (reset! state/server {:is-alive? true
                            :port      nil
                            :status    :running})
      (is (= service-unavailable-resp
             (sut/handler (mock/request :get "/health")))))
    (testing "has invalid port"
      (reset! state/server {:is-alive? true
                            :port      -5
                            :status    :running})
      (is (= service-unavailable-resp
             (sut/handler (mock/request :get "/health")))))
    (testing "has stopped"
      (server/start-server!)
      (server/stop-server!)
      (is (= service-unavailable-resp
             (sut/handler (mock/request :get "/health"))))

      (reset! state/server {:is-alive? true
                            :port      8080
                            :status    :stopped})
      (is (= service-unavailable-resp
             (sut/handler (mock/request :get "/health")))))
    (testing "is stopping"
      (reset! state/server {:is-alive? true
                            :port      8080
                            :status    :stopping})
      (is (= service-unavailable-resp
             (sut/handler (mock/request :get "/health")))))
    (testing "missing status"
      (reset! state/server {:is-alive? true
                            :port      8080
                            :status    nil})
      (is (= service-unavailable-resp
             (sut/handler (mock/request :get "/health"))))))))
