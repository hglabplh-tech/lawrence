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
                     (make-scan-result
                      [:decimal-symbol (Integer/parseInt lexeme)]
                      input input-position)))
                 (char-set:whitespace
                   (fn [lexeme position input input-position]
                     (make-scan-result [:whitespace]
                                       input input-position)))
                 ("*"
                   (fn [lexeme position input input-position]
                     (make-scan-result [:mul]
                                       input input-position)))
                 ("/"
                   (fn [lexeme position input input-position]
                     (make-scan-result [:div]
                                       input input-position)))
                 ("-"
                   (fn [lexeme position input input-position]
                     (make-scan-result [:minus]
                                       input input-position)))
                 ("+"
                   (fn [lexeme position input input-position]
                     (make-scan-result  [:plus]
                                       input input-position)))
                 ("**"
                   (fn [lexeme position input input-position]
                     (make-scan-result [:power]
                                       input input-position)))
                 ("("
                   (fn [lexeme position input input-position]
                     (make-scan-result [:lparen]
                                       input input-position)))
                 (")"
                   (fn [lexeme position input input-position]
                     (make-scan-result [:rparen]
                                       input input-position)))
                 ))
(gr/define-grammar
                    calculator
                    (:plus :minus :mul :div :lparen :rparen
                          :not :decimal-symbol)
                    expression
                    ((expression ((term) $1)
                                 ((:$error) 0)
                                 ((term :plus expression) (+ $1 $3))
                                 ((term :minus expression) (- $1 $3)))
                     (term ((product) $1)
                           ((product :mul term) (* $1 $3))
                           ((product :div term) (/ $1 $3)))
                     (product ((:decimal-symbol) $1)
                              ((:lparen
                                 expression :rparen) $2)
                              ((:lparen :$error :rparen) 0))

                     ))

(defn should-accept-string-data
  [pkg-sym scan-spec grammar data res]
  (is (= res (execute-direct pkg-sym
                             scan-spec grammar
                             data :lr)))
  (is (= res (execute-direct pkg-sym
                             scan-spec grammar
                             data :slr))))
(deftest do-direct
  (should-accept-string-data 'active.lawrence.functional-test
                 lang-scan calculator
                 "(9*5)" 45)

)

