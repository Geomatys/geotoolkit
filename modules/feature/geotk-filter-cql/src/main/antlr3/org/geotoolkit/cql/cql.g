grammar cql;

options {
    language = Java; // antlr will generate java lexer and parser
    output = AST; // generated parser should create abstract syntax tree
}

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


// GLOBAL STUFF ---------------------------------------

COMMENT : '#' ~('\n'|'\r')* '\r'? '\n' {$channel=HIDDEN;} ;

WS  :   ( ' '
        | '\t'
        | '\r'
        | '\n'
        ) {$channel=HIDDEN;}
    ;

// PROPERTY NAME -------------------------------------
PROPERTY_NAME
    :  '"' ( ESC_SEQ | ~('\\'|'"') )* '"'
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

//FUNCTION --------------------------------------------
//WORD :  (~( ' ' | '\t' | '\r' | '\n' | '(' | ')' | '"' ))+ ;

// FILTERING OPERAND -----------------------------------
EQUAL       : '=' ;
BETWEEN     : 'BETWEEN';
LIKE        : 'LIKE';
NULL        : 'NULL';


// LOGIC ----------------------------------------------
AND : 'AND' ;
OR : 'OR' ;
NOT : 'NOT' ;


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
    
    
//expression		: expression_function;
//expression_function 	: expression_operation | WORD '(' (expression (',' expression)+)* ')';
//expression_operation	: expression_simple | expression OPERATOR expression;
expression_simple	: PROPERTY_NAME | TEXT | INT | FLOAT;
    	
filter          : filter_and;
filter_and 	: filter_or (AND filter_and)*;
filter_or 	: filter_cb (OR filter_or)*;
filter_cb 	: expression_simple  WS*  (EQUAL)  WS*  expression_simple;
    	
result : expression_simple | filter ;

		



