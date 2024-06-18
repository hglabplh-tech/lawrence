(ns active.lawrence.generate-test
  (:require [active.lawrence.lr :as lr]
            [active.lawrence.process-test :refer :all]
            [clojure.test :refer :all]))


(defn generate
  [grammar name method]
  (lr/write-ds-parse-ns grammar 1 method
                        (symbol (str "active.lawrence." name "-parser"))
                        '([active.lawrence.parser-runtime :refer :all])
                        (str "./" name "_parser.clj")))

(generate toys-are-us "calc_it" :slr)