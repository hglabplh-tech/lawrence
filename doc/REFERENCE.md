## The parser and his functionality
 - Remark if you like further explanation look at
the documentation of Essence:
   
   [S48 Essence LR Parrser Generator Documentation](https://www.s48.org/essence/doc/html/essence.html)
 - 
- in that document there is also an general explanation about the way LR parser generators work.
- 
### Parser and history

The parser is a kind of implementation of an 
LR parser generator derived by idea from the 
essence parser generator written for Scheme48.

### Definition of a context free grammer

define-grammar in essence

```scheme
(define-grammar calc calculator
(+ - * / **  lparen rparen DIVISION
  PRODUCT SUBTRACT ADD POWER NEG)
expression
((expression  ((NUMBER) $1)
    ((expression expressionn ADD) 
      (+ $1 $2))
    ((expression expression SUBTRACT) 
      (- $1 $2))
    ((expression expreession PRODUCT) 
      (* $1 $2))
    ((expression expreession DIVISION) 
      (/ $1 $2))
    ((expression expression POWER) 
      (** $1 $2))
    ((expression NEG) 
      (- $1))
   ((lparen exp rparen) $2)
   )))
```

the same grammar in lawrence

```scheme
(define-grammar calculator
  (:+ :- :* :/ :** :lparent :rparent :DIVISION
    :PRODUCT :SUBTRACT :ADD :POWER :NEG)
  expression
  ((expression  ((NUMBER) $1)
     ((expression expressionn ADD) 
       (:+ $1 $2))
     ((expression expression SUBTRACT) 
       (:- $1 $2))
     ((expression expreession PRODUCT) 
       (:* $1 $2))
     ((expression expreession DIVISION) 
       (:/ $1 $2))
     ((expression expression POWER) 
       (:** $1 $2))
     ((expression NEG) 
       (:- $1))
     ((:lparen exp :rparen) $2)
     )))
```

as you see in the examples the one 
definition is very similar
to the other.

### The explanation of the grammars above
The expressions:
```scheme 
(+ - * / **)
```  
Essence (S48)
```scheme 
(:+ :- :* :/ :**)
```
Lawrence (Clojure)
define terminals

- The 'expression' in the line below the terminals 
is the start symbol.
- After that the rules for parsing follow:

#### Something about placeholders (variables)
- The $(n) where (n) is a integer defines a variable the (n) there defines the placement of the variable inside a term
- Example:
Lawrence (example above but as infix)
  ```scheme 
  ((expression ADD expression)
  (:+ $1 $3)) ;; here a addition is defined (infix) code: 5 + 3 - so the first and the third
  ;; placements defines variables -> $1 $3
  ```
#### The structure of a rule  