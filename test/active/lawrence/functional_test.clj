(ns active.lawrence.functional-test
  (:require [clojure.test :refer :all]
            [active.ephemerol.char-set :refer :all]
            [active.lawrence.grammar   :as gr]
            [active.lawrence.direct-lr :as direct]
            [active.ephemerol.regexp   :refer :all]
            [active.ephemerol.scanner  :refer :all]
            [active.ephemerol.scanner-run :refer :all]))

(defn eval-scanner
  [scanner sym-name-space]
  (binding [*ns* (find-ns sym-name-space)]
    (eval (scanner->expression scanner))))
(def method-lr :lr)
(def method-slr :slr)
(defn parse
  [g m vs]
  (direct/parse g 1 m vs))

(defn def-complete-scan [sym-name-space this-scanner-spec
                          input]

  (let [scanner (compute-scanner this-scanner-spec)
        scan-one (make-scan-one (eval-scanner scanner
                                              sym-name-space))

        scan-result
        (scan-to-list
          scan-one
          (string->list input)
          (make-position 1 0))]
    (println  "Scan Result: ")
    (println  scan-result)
    scan-result) )


(defn execute-direct [sym-name-space this-scanner-spec
                      yacc-result input method]


       (let [scan-result (def-complete-scan sym-name-space
                                            this-scanner-spec
                                            input)]


    (println  "Scan Result: ")
    (println  scan-result)
    (parse yacc-result method (first scan-result) )
    )
  )


(def lang-scan (scanner-spec
                  ((+ char-set:digit)
                   (fn [lexeme position input input-position]
                     (make-scan-result [:decimal-symbol (Integer/parseInt lexeme)]
                                       input input-position)))
                  (char-set:whitespace
                    (fn [lexeme position input input-position]
                      (make-scan-result :whitespace
                                        input input-position)))
                  ("*"
                    (fn [lexeme position input input-position]
                      (make-scan-result [:mul nil]
                                        input input-position)))
                  ("/"
                    (fn [lexeme position input input-position]
                      (make-scan-result [:div nil]
                                        input input-position)))
                  ("-"
                    (fn [lexeme position input input-position]
                      (make-scan-result [:minus nil]
                                        input input-position)))
                  ("+"
                    (fn [lexeme position input input-position]
                      (make-scan-result [:plus nil]
                                        input input-position)))
                  ("**"
                    (fn [lexeme position input input-position]
                      (make-scan-result [:power nil]
                                        input input-position)))
                  ("("
                    (fn [lexeme position input input-position]
                      (make-scan-result [:lparen nil]
                                        input input-position)))
                  (")"
                    (fn [lexeme position input input-position]
                      (make-scan-result [:rparen nil]
                                        input input-position)))
                  ("^"
                    (fn [lexeme position input input-position]
                      (make-scan-result [:not nil]
                                        input input-position)))))
 (def lang-grammar (gr/define-grammar
                     calculator
                     (list :plus :minus :mul :div :lparen :rparen
                           :not :decimal-symbol :power :whitespace)
                     expression
                     ((expression ((term) $1)
                                  ((term :plus expression) (+ $1 $3))
                                  ((term :power expression) (+ $1 $3))
                                  ((term :minus expression) (- $1 $3)))
                      (term ((product) $1)
                            ((term :mul term) (* $1 $3))
                            ((product :div term) (/ $1 $3)))
                      (product ((:not) $1)
                               ((:decimal-symbol) $1)
                               ((:lparen
                                  expression :rparen) $2))

                      )))
  (execute-direct 'active.lawrence.functional-test
                 lang-scan calculator
                  "(9 * 5)" method-slr)
