(ns active.lawrence.process-test
  (:require [clojure.test :refer :all]
            [active.lawrence.util.parse-lex-util :refer :all]
            [active.ephemerol.char-set :refer :all]
            [active.lawrence.grammar :as gr]
            [active.ephemerol.regexp :refer :all]
            [active.ephemerol.scanner :refer :all]
            [active.ephemerol.scanner-run :refer :all]))

(def toys-scan (scanner-spec
                 ((+ char-set:digit)
                  (fn [lexeme position input input-position]
                    (make-scan-result
                      [:decimal-symbol (Integer/parseInt lexeme) ]
                      input input-position)))
                 (char-set:whitespace
                   (fn [lexeme position input input-position]
                     (make-scan-result [:whitespace nil]
                                       input input-position)))
                 ("+"
                   (fn [lexeme position input input-position]
                     (make-scan-result [:+ nil]
                                       input input-position)))
                 ("-"
                   (fn [lexeme position input input-position]
                     (make-scan-result [:- nil]
                                       input input-position)))
                 ("*"
                   (fn [lexeme position input input-position]
                     (make-scan-result [:* nil]
                                       input input-position)))
                 ("/"
                   (fn [lexeme position input input-position]
                     (make-scan-result [:/ nil]
                                       input input-position)))
                 ("("
                   (fn [lexeme position input input-position]
                     (make-scan-result [:l nil]
                                       input input-position)))
                 (")"
                   (fn [lexeme position input input-position]
                     (make-scan-result [:r nil]
                                       input input-position)))
                 ("not"
                   (fn [lexeme position input input-position]
                     (make-scan-result [:n nil]
                                       input input-position)))
                 ))

(gr/define-grammar toys-are-us
                   (:+ :- :* :/ :l :r :n)
                   E
                   ((E ((T) $1)
                       ((:$error) 0)
                       ((T :+ E) (+ $1 $3))
                       ((T :- E) (- $1 $3)))
                    (T ((P) $1)
                       ((P :* T) (* $1 $3))
                       ((P :/ T) (/ $1 $3)))
                    (P ((:n) $1)
                       ((:l E :r) $2)
                       ((:l :$error :r) 0))))
(gr/define-grammar toys-are-us-ext
                   (:+ :- :* :/ :l :r :n :decimal-symbol)
                   E
                   ((E ((T) $1)
                       ((:$error) 0)
                       ((T :+ E) (+ $1 $3))
                       ((T :- E) (- $1 $3)))
                    (T ((P) $1)
                       ((P :* T) (* $1 $3))
                       ((P :/ T) (/ $1 $3)))
                    (P ((:n) $1)
                       ((:l E :r) $2)
                       ((:l :$error :r) 0))))

(deftest use-parse
  (let [expr (list [:l]  [:+] [:r])]
    (println expr)
(parse-with-mpair toys-are-us :lr expr)))

(deftest process-rest
  (execute-direct 'active.lawrence.process-test
                  toys-scan toys-are-us-ext
                  "(9*5)" method-lr))
