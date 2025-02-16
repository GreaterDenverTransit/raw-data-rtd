(ns web.handlers.healthcheck-test
  (:require [web.handlers.healthcheck :as sut]
            [clojure.test :refer [deftest is testing]]
            [ring.mock.request :as mock]
            [server.core :as server]
            [server.state :as state]))


(deftest healthcheck-success-test
  (testing "Healthcheck endpoint returns 200 when server is healthy"
    (server/start-server!)
    (is (= (sut/handler (mock/request :get "/health"))
           {:status  200
            :headers {"Content-Type" "text/json"}
            :body    {}}))
    (server/stop-server!)))

(deftest healthcheck-failure-test
  (testing "Healthcheck endpoint returns 503 when server"
    (let [service-unavailable-resp {:status  503
                                    :headers {"Content-Type" "text/json"}
                                    :body    {}}]
    (testing "not started"
      (is (= (sut/handler (mock/request :get "/health"))
             service-unavailable-resp)))
    (testing "not alive"
      (reset! state/server {:is-alive? false
                            :port      8080
                            :status    :running})
      (is (= (sut/handler (mock/request :get "/health"))
             service-unavailable-resp)))
    (testing "missing port"
      (reset! state/server {:is-alive? true
                            :port      nil
                            :status    :running})
      (is (= (sut/handler (mock/request :get "/health"))
             service-unavailable-resp)))
    (testing "has invalid port"
      (reset! state/server {:is-alive? true
                            :port      -5
                            :status    :running})
      (is (= (sut/handler (mock/request :get "/health"))
             service-unavailable-resp)))
    (testing "has stopped"
      (server/start-server!)
      (server/stop-server!)
      (is (= (sut/handler (mock/request :get "/health"))
             service-unavailable-resp))

      (reset! state/server {:is-alive? true
                            :port      8080
                            :status    :stopped})
      (is (= (sut/handler (mock/request :get "/health"))
             service-unavailable-resp)))
    (testing "is stopping"
      (reset! state/server {:is-alive? true
                            :port      8080
                            :status    :stopping})
      (is (= (sut/handler (mock/request :get "/health"))
             service-unavailable-resp)))
    (testing "missing status"
      (reset! state/server {:is-alive? true
                            :port      8080
                            :status    nil})
      (is (= (sut/handler (mock/request :get "/health"))
             service-unavailable-resp))))))
