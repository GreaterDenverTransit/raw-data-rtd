(ns config.core
  (:require [aero.core :as aero]
            [clojure.java.io :as io]))

(defn config
  []
  (aero/read-config (io/resource "config.edn")))

;; DB
(defn db [] (:db (config)))

;; Server
(defn server [] (:server (config)))
(defn address [] (:address (server)))
(defn port [] (:port (server)))

;; Defaults
(defn default-count [] (:default-count (config)))
