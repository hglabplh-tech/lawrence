(ns active.lawrence.functional-test
  (:require [clojure.test :refer :all]
            [active.lawrence.util.parse-lex-util :refer :all]
            [active.ephemerol.char-set :refer :all]
            [active.lawrence.grammar :as gr]
            [active.ephemerol.regexp :refer :all]
            [active.ephemerol.scanner :refer :all]
            [active.ephemerol.scanner-run :refer :all]))


(def lang-scan (scanner-spec
                 ((+ char-set:digit)
                  (fn [lexeme position input input-position]
                    ( (make-scan-result
                      [(Integer/parseInt lexeme) :decimal-symbol]
                      input input-position)))
                 (char-set:whitespace
                   (fn [lexeme position input input-position]
                     (make-scan-result [lexeme :whitespace]
                                       input input-position)))
                 ("*"
                   (fn [lexeme position input input-position]
                     (make-scan-result [lexeme :mul]
                                       input input-position)))
                 ("/"
                   (fn [lexeme position input input-position]
                     (make-scan-result [lexeme :div]
                                       input input-position)))
                 ("-"
                   (fn [lexeme position input input-position]
                     (make-scan-result [lexeme :minus]
                                       input input-position)))
                 ("+"
                   (fn [lexeme position input input-position]
                     (make-scan-result () [lexeme :plus]
                                       input input-position)))
                 ("**"
                   (fn [lexeme position input input-position]
                     (make-scan-result [lexeme :power]
                                       input input-position)))
                 ("("
                   (fn [lexeme position input input-position]
                     (make-scan-result [lexeme :lparen]
                                       input input-position)))
                 (")"
                   (fn [lexeme position input input-position]
                     (make-scan-result [lexeme :rparen]
                                       input input-position)))
                 ("^"
                   (fn [lexeme position input input-position]
                     (make-scan-result [lexeme :not]
                                       input input-position)))))
(def lang-grammar (gr/define-grammar
                    calculator
                    (list :plus :minus :mul :div :lparen :rparen
                          :not :decimal-symbol :power :whitespace)
                    expression
                    ((expression ((expression term) $1 $2)
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
;;(parse calculator :lr input)

