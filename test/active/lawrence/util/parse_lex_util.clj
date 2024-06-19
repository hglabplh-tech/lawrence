(ns active.lawrence.util.parse-lex-util
  (:require [active.ephemerol.scanner :refer :all]
            [active.ephemerol.scanner-run :refer :all]
            [active.lawrence.direct-lr :as direct]
            [active.lawrence.grammar :as gr]
            [active.lawrence.runtime :as rt]))

(defn input
  "Convert list of [token attribute-value] vectors to input."
  [g vs]
  (map (fn [[t av]]
         (rt/make-pair (gr/grammar-name->symbol t g)
                       av))
       vs))

(defn eval-scanner
  [scanner sym-name-space]
  (binding [*ns* (find-ns sym-name-space)]
    (eval (scanner->expression scanner))))

(defn parse
  [g m vs]
    (direct/parse g 1 m (input g vs)))

(defn def-complete-scan [sym-name-space this-scanner-spec
                         input]
  (let [scanner (compute-scanner this-scanner-spec)
        scan-one (make-scan-one (eval-scanner scanner sym-name-space))
        scan-result (scan-to-list
                      scan-one
                      (string->list input)
                      (make-position 1 0))]
    (first scan-result)))

(defn execute-direct [sym-name-space this-scanner-spec
                      yacc-result input method]
  (let [output (apply list
                      (def-complete-scan sym-name-space this-scanner-spec input))]
    (parse yacc-result method output)))