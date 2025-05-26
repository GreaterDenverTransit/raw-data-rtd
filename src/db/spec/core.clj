(ns db.spec.core
  "Generic specs")

(def non-empty-string [:string {:min 1}])

(def latitude [:double {:min -90.0 :max 90.0}])
(def longitude [:double {:min -180.0 :max 180.0}])
