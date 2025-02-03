(ns config.core
  (:require [aero.core :as aero]
            [clojure.java.io :as io]))

(aero/read-config (io/resource "config.edn"))
