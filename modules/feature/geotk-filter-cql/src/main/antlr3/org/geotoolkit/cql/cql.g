grammar cql;

options {
    language = Java; // antlr will generate java lexer and parser
    output = AST; // generated parser should create abstract syntax tree
    //backtrack = true;
}

//-----------------------------------------------------------------//
// JAVA CLASS GENERATION
//-----------------------------------------------------------------//

//force header on generated classes
@lexer::header {
  package org.geotoolkit.cql;
}
@parser::header {
  package org.geotoolkit.cql;
}

//catch errors
@lexer::members {
  private List<RecognitionException> errors = new ArrayList<RecognitionException>();
  public List<RecognitionException> getAllErrors() {
    return new ArrayList<RecognitionException>(errors);
  }
  public boolean hasErrors() {
    return !errors.isEmpty();
  }
  public void reportError(RecognitionException e) {
    errors.add(e);
  }  
}

//catch errors
@parser::members {
  private List<RecognitionException> errors = new ArrayList<RecognitionException>();
  public List<RecognitionException> getAllErrors() {
    return new ArrayList<RecognitionException>(errors);
  }
  public boolean hasErrors() {
    return !errors.isEmpty();
  }
  public void reportError(RecognitionException e) {
    errors.add(e);
  }
}

//-----------------------------------------------------------------//
// LEXER
//-----------------------------------------------------------------//


// GLOBAL STUFF ---------------------------------------

COMMENT : '#' ~('\n'|'\r')* '\r'? '\n' {$channel=HIDDEN;} ;

WS  :   ( ' '
        | '\t'
        | '\r'
        | '\n'
        ) {$channel=HIDDEN;}
    ;

//LITERALS  ----------------------------------------------

TEXT
    :   '\'' ( ESC_SEQ | ~('\\'|'\'') )* '\''
    ;
    
INT : '0'..'9'+ ;

FLOAT
    :   ('0'..'9')+ '.' ('0'..'9')* EXPONENT?
    |   '.' ('0'..'9')+ EXPONENT?
    |   ('0'..'9')+ EXPONENT
    ;

//OPERATORS -------------------------------------------
OPERATOR: '+' | '-' | '/' | '*' | '%' ; 



// FILTERING OPERAND -----------------------------------
EQUALABOVE: '>=' ;
EQUALUNDER: '<=' ;
NOTEQUAL	: '<>' ;
EQUAL	: '=' ;
ABOVE	: '>' ;
UNDER	: '<' ;
LIKE       	: L I K E;
NULL        	: N U L L;
BETWEEN     	: B E T W E E N;


// LOGIC ----------------------------------------------
AND 	: A N D;
OR 	: O R ;
NOT 	: N O T ;

// PROPERTY NAME -------------------------------------
PROPERTY_NAME_1
    :  '"' ( ESC_SEQ | ~('\\'|'"') )* '"'
    ;
    
PROPERTY_NAME_2
   : WORD 
   ;
   

// FRAGMENT -------------------------------------------

fragment
EXPONENT : ('e'|'E') ('+'|'-')? ('0'..'9')+ ;

fragment
HEX_DIGIT : ('0'..'9'|'a'..'f'|'A'..'F') ;

fragment
ESC_SEQ
    :   '\\' ('b'|'t'|'n'|'f'|'r'|'\"'|'\''|'\\')
    |   UNICODE_ESC
    |   OCTAL_ESC
    ;

fragment
OCTAL_ESC
    :   '\\' ('0'..'3') ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7')
    ;

fragment
UNICODE_ESC
    :   '\\' 'u' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT
    ;
    
fragment
WORD 	:(~( ' ' | '\t' | '\r' | '\n' | '(' | ')' | '"' ))+ ;
    
 // caseinsensitive , possible alternative solution ?
fragment A: ('a'|'A');
fragment B: ('b'|'B');
fragment C: ('c'|'C');
fragment D: ('d'|'D');
fragment E: ('e'|'E');
fragment F: ('f'|'F');
fragment G: ('g'|'G');
fragment H: ('h'|'H');
fragment I: ('i'|'I');
fragment J: ('j'|'J');
fragment K: ('k'|'K');
fragment L: ('l'|'L');
fragment M: ('m'|'M');
fragment N: ('n'|'N');
fragment O: ('o'|'O');
fragment P: ('p'|'P');
fragment Q: ('q'|'Q');
fragment R: ('r'|'R');
fragment S: ('s'|'S');
fragment T: ('t'|'T');
fragment U: ('u'|'U');
fragment V: ('v'|'V');
fragment W: ('w'|'W');
fragment X: ('x'|'X');
fragment Y: ('y'|'Y');
fragment Z: ('z'|'Z');
    
    
//-----------------------------------------------------------------//
// PARSER
//-----------------------------------------------------------------//
    
    
expression		: expression_simple;
//expression_function 	: expression_operation | WORD '(' (expression (',' expression)+)* ')';
//expression_operation	: expression_simple (OPERATOR expression_operation)* ;
expression_simple	: PROPERTY_NAME_1 | PROPERTY_NAME_2 |  TEXT | INT | FLOAT;
    	
filter          	: filter_and;
filter_and 	: filter_or (AND filter_and)*;
filter_or 	: filter_cb (OR filter_and)*;
filter_cb 	: expression  (EQUAL|LIKE)  expression;
 
    	
result : expression | filter ;

		



