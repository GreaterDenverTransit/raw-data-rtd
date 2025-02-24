(ns domain.boardings
  (:require [db.boardings :as boardings-db]))

(def boardings-atom (atom nil))

(defn coerce-date
  "Strips `date` of its \"part of week\" section to make filtering easier"
  [date]
  (subs date 0 5))

(defn coerce-count
  "Ensures `count'` is a long"
  [count']
  (if (number? count')
    count'
    (parse-long count')))

(defn top-n
  [count' start-date end-date]
  (swap! boardings-atom assoc :top-n {:count count' :start-date start-date :end-date end-date})
  (boardings-db/top-n (coerce-count count')
                      (coerce-date start-date)
                      (coerce-date end-date)))

(defn bottom-n
  [count' start-date end-date]
  (boardings-db/bottom-n (coerce-count count')
                         (coerce-date start-date)
                         (coerce-date end-date)))

(defn- asc?
  "True iff `order` should be ascending"
  [order]
  (= "asc" (name order)))

(defn- count?
  [count']
  (and count' (pos? count')))

(defn- order?
  [order]
  (let [order' (name order)]
    (or (= "asc" order')
        (= "desc" order'))))

;; TODO
;; Questionable whether dates should be as they appear in DB or have
;; (day(s) of week omitted), in theory we don't need these but sending a standard
;; date format over the wire across different requests may be advantageous
(defn- date?
  [date]
  (->> date
       (re-matches #"[A-Z][a-z]{2}[0-9]{2} \((?:Friday|Saturday|Sunday|Weekday)\)")
       string?))

;; TODO:
(defn- before?
  [date0 date1]
  true)

(defn- valid-boardings?
  [{:keys [count' order start-date end-date]}]
  (and (count? count')
       (order? order)
       (date? start-date)
       (date? end-date)
       (before? start-date end-date)))

(defn boardings
  [{:keys [count' order start-date end-date] :as boardings}]
  (println "in boardings")
  (reset! boardings-atom {:boardings boardings})
  (when (valid-boardings? boardings)
    (if (asc? #p order)
      (top-n count' start-date end-date)
      (bottom-n count' start-date end-date))))
