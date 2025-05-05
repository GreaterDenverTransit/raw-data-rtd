(ns combine-db-partitions
  (:require [clojure.string :as str]
            [clojure.java.shell :as sh]))

(defn concat-db-files!
  [& {:keys [dry?]}]
  (sh/with-sh-dir
    "resources/db/db_partitions2"
    (let [{:keys [out]} (sh/sh "ls" "-a")
          files (->> out
                     str/split-lines
                     (remove (partial re-matches #".*\..*"))
                     sort)
          batches (partition-all 15 files)
          f (or (and dry? println) sh/sh)]

      (spit
       "resources/db/db_partitions2/concatenated_db.db"
       (:out (apply
              f
              "cat"
              files)))

      #_(if dry?
        (do
          (apply
           f
           "cat"
           (concat
            files
            [">" "concatenated_db.db"]))
          (shutdown-agents))
        (reduce
         (fn [{:keys [tmp batch-files]} cur-batch]
           (prn "tmp" tmp)
           (spit
            (str "batch_" tmp)
            (:out (apply
                   f
                   "cat"
                   cur-batch)))
           {:tmp         (inc tmp)
            :batch-files (conj batch-files (str "batch_" tmp))})
         {:tmp         0
          :batch-files []}
         batches)))))
