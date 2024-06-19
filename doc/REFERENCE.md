## The parser and his functionality
 - Remark if you like further explanation about left-to-right (shift - reduce parsers) look at
the documentation of Essence:
   
   [S48 Essence LR Parrser Generator Documentation](https://www.s48.org/essence/doc/html/essence.html)
- in that document there is also an general explanation about the way LR parser generators work.
### Parser and history

Lawrence is a implementation of an 
LR / SLR parser generator derived from the 
Essence parser generator written in Scheme-48.

### Definition of a context free grammer


the same grammar in lawrence

```scheme
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
```scheme 
(:+ :- :* :/ :**)
```


- The 'expression' in the line below the terminals 
is the start symbol. Thjis symbol is needed by the generator to determine the start of the grammar
- After that the rules for parsing follow:

#### Something about placeholders (variables)
- The $(n) where (n) is a integer defines a variable the (n) there defines the placement of the variable inside a term
- Example:
Lawrence (example above but as infix)
  ```scheme 
   ((term :plus expression) (+ $1 $3))
  ```
here a addition is defined (infix) code: 5 + 3 - so the first and the third placements defines variables -> $1 $3
#### The structure of a rule

```scheme
1. ((expression ((term) $1) 
2.      ((:$error) 0).....))
```
- 1: here the **_rule_** for building a **expression** out of a given **term** which is defined later
- 2: here the **_rule_** what happens in case of a parser error is defined the **_rule_** tells us that we simply go on in case of a error


 ```scheme 
  3. ((term :plus expression) (+ $1 $3)) 
  ```
- 3: and now after we defined a **expression** by a term we define that a **term** and a **terminal**  

```scheme
 (term ((product) $1)... )
```

- 4: with this **_rule_** we define a **term** in final as a result of the definition of **product**
```scheme
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

