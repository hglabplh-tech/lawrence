## The parser and his functionality
 
#### - for those who are interested in a better understanding of shift - reduce parser or are not familiar with it:

Shift Reduce parser attempts for the construction of parse in a 
similar manner as done in bottom-up parsing i.e. the parse tree is 
constructed from leaves(bottom) to the root(up). 
A more general form of the shift-reduce parser is the LR parser.

##### This parser requires some data structures i.e.

- _An input buffer for storing the input string._
- _A stack for storing and accessing the production rules._

Out of an article of (geeks for geeks (Author not mentioned))
[Shift Reduce - parser compiler](https://www.geeksforgeeks.org/shift-reduce-parser-compiler/)

**REMARK:** here is the documentation of Essence where also a good explanation can be found:
 [S48 Essence LR Parser Generator Documentation](https://www.s48.org/essence/doc/html/essence.html)

- in that document also the specific documentation can be found and the way the Lawrence  
  parser generator work.
- 
### Parser and history

Lawrence is a implementation of an 
LR / SLR parser generator derived from the 
Essence parser generator written in Scheme-48.
Written by Peter Thieman, Mike Sperber

### Definition of a context free grammer

##### - Example of a grammar we use to explain the steps:

```clojure
(define-grammar
  calculator
  (:plus :minus :mul :div :lparen :rparen
    :not :decimal-symbol :power :whitespace)
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
```

### The explanation of the grammars above

The expressions:

- define terminals
```clojure 
(:+ :- :* :/ :**)
```


- The 'expression' in the line below the terminals 
is the start symbol. Thjis symbol is needed by the generator to determine the start of the grammar
- After that the rules for parsing follow:

#### Something about placeholders (variables)
- The $(n) where (n) is a integer defines a variable the (n) there defines the placement of the variable inside a term
- Example:
Lawrence (example above but as infix)
  ```clojure
   ((term :plus expression) (+ $1 $3))
  ```
here a addition is defined (infix) code: 5 + 3 - so the first and the third placements defines variables -> $1 $3
#### The structure of a rule

```clojure
1. ((expression ((term) $1) 
2.      ((:$error) 0).....))
```
- 1: here the **_rule_** for building a **expression** out of a given **term** which is defined later
- 2: here the **_rule_** what happens in case of a parser error is defined the **_rule_** tells us that we simply go on in case of a error


 ```clojure
  3. ((term :plus expression) (+ $1 $3)) 
  ```
- 3: and now after we defined a **expression** by a term we define that a **term** and a **terminal**  

```clojure
 (term ((product) $1)... )
```

- 4: with this **_rule_** we define a **term** in final as a result of the definition of **product**
```clojure
 (product ((:decimal-symbol) $1)...)
```

- 5: what we need now is that we define the **product** by using the smallest unit 
(in our case the **number**) to be sure that
any rule we defined ends in a defined **_terminal/ token_**.

### Some code examples how to call lawrence and test it direct or generate a specialized parser
[Code to call lawrence parse](https://github.com/cresh/parser/tree/main)

#### **Remark :** 
The definition of the terminals for this example is done in a scanner specification
written with macros from ephemerol. See the `**_de.active-group/ephemerol_**` project
on GitHub: [**_Ephemerol_**](https://github.com/active-group/ephemerol)

#### Here some code explained:
This code is part of a project Marcus Crestani set up to simplify the call of ephemerol / lawrence in a way 
that it is possible to call the parser directly or to generate a specialized parser in
the context of having a given scanner spec. .

- Here a base macro creating a scanner with output matching to lawrence:
```clojure
(defmacro make-scan-result-for-lawrence
  [?enum ?lexeme->attribute] ...)
```

- with this definition more specialized forms can be defined:

1. definition for a keyword with no parameter

```clojure
(defmacro keyword-result
  [?enum]
  `(make-scan-result-for-lawrence ...)) 
```

2. definition for a keyword with one parameter (e.g. a number )

```clojure
defmacro number-result
  []
  `(make-scan-result-for-lawrence :number ,@_) .... 
```

Here is a simple scanner definition with tese simplifying macros:

```clojure
;; scanner specification
(def scanner-specification
  (scanner/scanner-spec
    ;; skip whitespace
    (char-set/char-set:whitespace
    (fn [_lexeme _position input input-position]
      (scan-one input input-position)))

    ;; keywords
    ("+" (keyword-result :+))
    ("-" (keyword-result :-))
    ("*" (keyword-result :*))
    ("/" (keyword-result :/))
    ("(" (keyword-result :l))
    (")" (keyword-result :r))

    ;; numbers
    ((+ char-set/char-set:digit) (number-result))))

```

A more detailed explanation by Marcus Crestani will be available later on 
[Functional development BLOG](https://funktionale-programmierung.de)

### How to write code making the parser generator working

```clojure
;; here the real functionality of parse in lawrence is called
(defn parse
  [input]
  (lr-parser/parse grammar 1 :slr input))

;; here the scan result is given to the parser to get both connected 
;; and to get a correct parser in cooperation with the scanner
;; scan and parse input
(def scan+parse
  (comp parse scan))

;; just an example to see if the whole thing is working
(scan+parse "1+2*(65/5)")
```

And to be complete here again the scanner-generation of ephemorol
See []() for documentation

```clojure
(defn scan
  [input]
  (let [scanned (scanner-run/scan-to-list scan-one (scanner-run/string->list input)
                                          (scanner-run/make-position 1 0))
        scan-result (first scanned)]
    (if (scanner-run/scan-error? scan-result)
      ;; handle scan error
      (throw (Exception. (pr-str scanned)))
      scan-result)))

```

et voila we have both connected up and running

### How to store a specialized parser in connection with ephemerol on disk "blah_parser.clj"

```clojure
 (lr-parser/write-ds-parse-ns core/grammar 1 :slr
                              
                              ;; here the namespace as symbol
                              'parser.generated-parser
                              ;; the needed requiring for the generated specialized 
                              ;; parser 
                               '([parser.generate :refer [parse-error]])
                               "src/parser/generated_parser.clj"))
```


**_Will be continued_** ......

