(ns test.config
  (:require [aero.core :as aero]
            [clojure.java.io :as io]))

(defn config
  []
  (aero/read-config (io/resource "test-config.edn")))

;; DB
(defn db [] (:db (config)))
