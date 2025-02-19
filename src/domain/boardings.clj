(ns domain.boardings)

(defn top-n
  [count' start-date end-date])

(defn bottom-n
  [count' start-date end-date])

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
  (when (valid-boardings? boardings)
    (if (asc? order)
      (top-n count' start-date end-date)
      (bottom-n count' start-date end-date))))
