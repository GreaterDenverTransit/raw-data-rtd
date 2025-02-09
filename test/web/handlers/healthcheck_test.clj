(ns web.handlers.healthcheck-test
  (:require [web.handlers.healthcheck :as sut]
            [bond.james :as bond]
            [clojure.test :as t]
            [compojure.core :refer [GET]]))
