(ns utils
  (:require [camel-snake-kebab.core :as csk]))

(def ->snake_case_symbol
  (memoize csk/->snake_case_symbol))

(def ->kebab-case-keyword
  (memoize csk/->kebab-case-keyword))
