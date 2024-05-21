(define-grammar fortran77 fortran77-symbol
  (:executableUnit)
  program
  ((program ((executableUnit) $1)
     (((functionSubprogram) $2)
       ((mainProgram) $2)
       ((subroutineSubprogram) $2)
       ((blockdataSubprogram))))
     (((mainProgram) $3)
       ((programStatement) subprogramBody)
       ((subProgramBody) $4)
     ))
    )