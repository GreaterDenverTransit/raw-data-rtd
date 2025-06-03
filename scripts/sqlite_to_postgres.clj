(ns sqlite-to-postgres
  (:require [clojure.string :as str]
            [clojure.tools.cli :refer [parse-opts]]
            [utils :refer [partial-right]]))

(def cli-options
  [["-i" "--in IN" "Input sqlite DB file"
    :validate [(partial-right str/ends-with? ".db")]]])

(defn -main
  [& args]
  (parse-opts args cli-options))
