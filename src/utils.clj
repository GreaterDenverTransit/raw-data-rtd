(ns utils
  (:require [camel-snake-kebab.core :as csk]))

(def ->snake_case_symbol
  (memoize csk/->snake_case_symbol))

(def ->kebab-case-keyword
  (memoize csk/->kebab-case-keyword))

(defn prn-err
  [& args]
  (binding [*out* *err*]
    (apply prn args)))

(defn partial-right
  "Takes a function `f` and fewer than the normal arguments to `f`, and
  returns a fn that takes a variable number of additional args. When
  called, the returned function calls `f` with additional args + args."
  ([f] f)
  ([f arg0]
   (fn
     ([] (f arg0))
     ([x] (f x arg0))
     ([x y] (f x y arg0))
     ([x y z] (f x y z arg0))
     ([x y z & args] (apply f x y z (concat args arg0)))))
  ([f arg0 arg1]
   (fn
     ([] (f arg0 arg1))
     ([x] (f x arg0 arg1))
     ([x y] (f x y arg0 arg1))
     ([x y z] (f x y z arg0 arg1))
     ([x y z & args] (apply f x y z (concat args [arg0 arg1])))))
  ([f arg0 arg1 arg2]
   (fn
     ([] (f arg0 arg1 arg2))
     ([x] (f x arg0 arg1 arg2))
     ([x y] (f x y arg0 arg1 arg2))
     ([x y z] (f x y z arg0 arg1 arg2))
     ([x y z & args] (apply f x y z (concat args [arg0 arg1 arg2])))))
  ([f arg0 arg1 arg2 & more]
   (fn [& args] (apply f (concat more args [arg0 arg1 arg2])))))
