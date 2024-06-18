(ns active.lawrence.util.parse-lex-util
  (:require [active.ephemerol.char-set :refer :all]
            [active.lawrence.grammar   :as gr]
            [active.lawrence.runtime   :as rt]
            [active.lawrence.direct-lr :as direct]
            [active.ephemerol.regexp   :refer :all]
            [active.ephemerol.scanner  :refer :all]
            [active.ephemerol.scanner-run :refer :all]))
(defn input
  "Convert list of [token attribute-value] vectors to input."
  [g vs]
  (map (fn [[t av]]
         ;;(rt/make-pair (gr/grammar-name->symbol t g)
         ;;   av
         (rt/make-pair (gr/grammar-name->symbol t  g)
         av))
       vs))


(defn eval-scanner
  [scanner sym-name-space]
  (binding [*ns* (find-ns sym-name-space)]
    (eval (scanner->expression scanner))))

(def method-lr :lr)
(def method-slr :slr)

(defn parse
  [g m vs]
  (let [cooked-input (input g vs)]
    (println cooked-input)
  (direct/parse g 1 m cooked-input)))



(defn def-complete-scan [sym-name-space this-scanner-spec
                         input]

  (let [scanner (compute-scanner this-scanner-spec)
        scan-one (make-scan-one (eval-scanner scanner
                                              sym-name-space))


        scan-result

         (scan-to-list
        scan-one
        (string->list input)
        (make-position 1 0))
    ]
    (println  "Scan Result output: ")
    (println  (first scan-result))
    (first scan-result)) )


(defn execute-direct [sym-name-space this-scanner-spec
                      yacc-result input method]


  (let [output (apply list (def-complete-scan sym-name-space
                                       this-scanner-spec
                                       input))]



    (println  "Scan Result: ")
    (println  output)
    (parse yacc-result method  output                           ;;(first scan-result)
                                      ))
  )
