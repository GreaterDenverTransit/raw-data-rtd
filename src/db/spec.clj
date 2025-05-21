(ns db.spec)

;; generic specs
(def non-empty-string [:string {:min 1}])

;; domain specs
(def direction-name
  [:enum
   "N-Bound"
   "S-Bound"
   "E-Bound"
   "W-Bound"
   "Civic Ctr"
   "Union Stn"
   "Clock"
   "Counterclock"])

(def service-type
  [:enum
   ;; FF(1-7)
   "Bus Rapid Transit"
   ;; A, B, G, N
   "Commuter Rail"
   ;; C, D, E, F, L, R, W
   "Light Rail"
   "Local"
   ;; Both "Mall" and "Mall Shuttle" refer to the free mallride, the former was
   ;; internally changed to the latter by RTD for 2024 onward
   "Mall"
   "Mall Shuttle"
   "MetroRide"
   "Regional"
   ;; ATA
   "SkyRide"])

(def service-mode
  [:enum
   "Bus"
   "Bus Rapid Transit"
   "Commuter Rail"
   "Light Rail"])

(def time-period
  [:enum
   "AM Early"
   "AM Peak"
   "Midday"
   "Other"
   "PM Evening"
   "PM Late"
   "PM Peak"])

(def schedule-days
  [:enum
   "FR"
   "MT"
   "SA"
   "SU"
   "WK"])

(def combined-ridership-data
  [:map
   [:alightings [:int {:min 0}]]
   [:boardings [:int {:min 0}]]
   [:branch non-empty-string]
   [:direction-name direction-name]
   [:direction-number [:maybe [:enum 0 1]]]
   [:load [:maybe :int]]
   [:route non-empty-string]
   [:schedule-name non-empty-string]
   [:service-mode service-mode]
   [:service-type [:maybe service-type]]
   [:sort-order pos-int?]
   [:stop-id pos-int?]
   [:schedule-days schedule-days]
   ;; YYYY_MM_WD where WD ∈ schedule-days
   ;; ex: 2019_04_FR
   [:service-id [:re #"[0-9]{4}_[0-1][0-9]_(FR|MT|SA|SU|WK)"]]
   [:schedule-month pos-int?]
   ;; a bit generous with the range but don't want generators generating wildly
   ;; unrealistic numbers
   [:schedule-year [:int {:min 1969 :max 2999}]]
   [:time-period time-period]
   [:trip-id pos-int?]])
