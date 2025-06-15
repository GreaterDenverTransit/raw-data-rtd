(ns test.utils-test
  (:require [clojure.test :refer [deftest is]]
            [utils :as sut]))

(deftest test-partial-right
  (let [p0 (sut/partial-right inc)
        p1 (sut/partial-right + 20)
        p2 (sut/partial-right cons [1 2])
        p3 (sut/partial-right / 1 2 3)]
    (is (= 41 (p0 40)))
    (is (= 40 (p1 20)))
    (is (= '(3 1 2) (p2 3)))
    (is (= 5/6 (p3 5)))))
