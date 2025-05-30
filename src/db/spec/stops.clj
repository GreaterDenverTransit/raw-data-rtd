(ns db.spec.stops
  (:require [db.spec.core :refer [latitude longitude non-empty-string]]))

(def stop-desc
  [:enum
   "Vehicles Travelling East"
   "Vehicles Travelling North"
   "Vehicles Travelling Northeast"
   "Vehicles Travelling Northwest"
   "Vehicles Travelling South"
   "Vehicles Travelling Southeast"
   "Vehicles Travelling Southwest"
   "Vehicles Travelling West"
   "Vehicles Travelling"])

(def stops
  [:map
   [:location-type [:enum 0 1]]
   [:parent-station [:maybe :string]]
   [:stop-code pos-int?]
   [:stop-desc stop-desc]
   [:stop-id pos-int?]
   [:stop-lat latitude]
   [:stop-lon longitude]
   [:stop-name non-empty-string]
   ;; RTD doesn't use this field but includes it to match a standard spec
   [:stop-timezone nil?]
   [:stop-url [:re #"https://app.rtd-denver.com/nextride/stop/\d+"]]
   [:wheelchair-boarding [:enum 0 1]]
   [:zone-id [:maybe non-empty-string]]])
