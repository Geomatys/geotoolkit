/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012-2014, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
grammar CQL;

options {
    language = Java;
}

//-----------------------------------------------------------------//
// LEXER
//-----------------------------------------------------------------//


// GLOBAL STUFF ---------------------------------------

COMMA 	: ',' ;
WS  :   ( ' ' | '\t' | '\r'| '\n' ) -> skip;
UNARY : '+' | '-' ;
MULT : '*' | '/' ;
fragment DIGIT : '0'..'9' ;
    
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
fragment LETTER : ~('0'..'9' | ' ' | '\t' | '\r'| '\n' | ',' | '-' | '+' | '*' | '/' | '(' | ')' | '=' | '>' | '<');

LPAREN : '(';
RPAREN : ')';
    

//LITERALS  ----------------------------------------------

TEXT :   '\'' ( ESC_SEQ | ~('\'') )* '\'' ;   
INT : DIGIT+ ;

FLOAT
    :   ('0'..'9')+ '.' ('0'..'9')* EXPONENT?
    |   '.' ('0'..'9')+ EXPONENT?
    |   ('0'..'9')+ EXPONENT
    ;


// FILTERING OPERAND -----------------------------------
COMPARE 
	: EQUALABOVE
	| EQUALUNDER
	| NOTEQUAL
	| EQUAL
	| ABOVE
	| UNDER
	;
fragment EQUALABOVE : '>=' ;
fragment EQUALUNDER : '<=' ;
fragment NOTEQUAL   : '<>' ;
fragment EQUAL      : '=' ;
fragment ABOVE      : '>' ;
fragment UNDER      : '<' ;
LIKE    : L I K E;
ILIKE   : I L I K E;
	
IS      : I S ;
NULL    : N U L L ;
BETWEEN : B E T W E E N;
IN      : I N;



// LOGIC ----------------------------------------------
AND : A N D;
OR  : O R ;
NOT : N O T ;

// GEOMETRIC TYPES AND FILTERS ------------------------
POINT               : P O I N T ;
LINESTRING          : L I N E S T R I N G ;
POLYGON             : P O L Y G O N ;
MPOINT              : M U L T I P O I N T ;
MLINESTRING         : M U L T I L I N E S T R I N G ;
MPOLYGON            : M U L T I P O L Y G O N ;
GEOMETRYCOLLECTION  : G E O M E T R Y C O L L E C T I O N ;
ENVELOPE            : E N V E L O P E ;
EMPTY               : E M P T Y ;

BBOX        : B B O X ;
BEYOND      : B E Y O N D ;
CONTAINS    : C O N T A I N S ;
CROSSES     : C R O S S E S;
DISJOINT    : D I S J O I N T ;
DWITHIN     : D W I T H I N ;
EQUALS      : E Q U A L S ;
INTERSECTS  : I N T E R S E C T S;
OVERLAPS    : O V E R L A P S;
TOUCHES     : T O U C H E S;
WITHIN      : W I T H I N ;

// TEMPORAL TYPES AND FILTERS

DATE : DIGIT DIGIT DIGIT DIGIT '-' DIGIT DIGIT '-' DIGIT DIGIT 'T' DIGIT DIGIT ':' DIGIT DIGIT ':' DIGIT DIGIT ('.' DIGIT+)? 'Z';
DURATION_P : P (INT 'Y')? (INT 'M')? (INT 'D')? (INT 'H')? (INT 'M')? (INT 'S')?; 
DURATION_T : T (INT 'H')? (INT 'M')? (INT 'S')?;

AFTER		: A F T E R ;
ANYINTERACTS	: A N Y I N T E R A C T S ;
BEFORE		: B E F O R E ;
BEGINS		: B E G I N S ;
BEGUNBY		: B E G U N B Y ;
DURING		: D U R I N G ;
ENDEDBY		: E N D E D B Y ;
ENDS		: E N D S ;
MEETS		: M E E T S ;
METBY		: M E T B Y ;
OVERLAPPEDBY	: O V E R L A P P E D B Y ;
TCONTAINS	: T C O N T A I N S ;
TEQUALS		: T E Q U A L S ;
TOVERLAPS	: T O V E R L A P S ;


// PROPERTY NAME -------------------------------------
PROPERTY_NAME    	:  '"' ( ESC_SEQ | ~('\\'|'"') )* '"'    ;
NAME   	: LETTER (DIGIT|LETTER)* ;
   

// FRAGMENT -------------------------------------------

fragment EXPONENT : ('e'|'E') ('+'|'-')? ('0'..'9')+ ;
fragment HEX_DIGIT : ('0'..'9'|'a'..'f'|'A'..'F') ;

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
        
    
    
    
//-----------------------------------------------------------------//
// PARSER
//-----------------------------------------------------------------//
    
expressionNum : INT | FLOAT ;	
expressionUnary : UNARY? expressionNum ;
	
coordinate          : expressionUnary expressionUnary ;
coordinateSerie    : LPAREN coordinate (COMMA coordinate)*  RPAREN ;
coordinateSeries   : LPAREN coordinateSerie (COMMA coordinateSerie)* RPAREN;

expressionGeometry	
	: POINT ( EMPTY | coordinateSerie )
	| LINESTRING ( EMPTY | coordinateSerie )
	| POLYGON ( EMPTY | coordinateSeries )
	| MPOINT ( EMPTY | coordinateSerie )
	| MLINESTRING  ( EMPTY | coordinateSeries )
	| MPOLYGON ( EMPTY | LPAREN coordinateSeries (COMMA coordinateSeries)* RPAREN )
        | GEOMETRYCOLLECTION ( EMPTY | (LPAREN expressionGeometry (COMMA expressionGeometry)* RPAREN) )
        | ENVELOPE ( EMPTY | (LPAREN expressionUnary COMMA expressionUnary COMMA expressionUnary COMMA expressionUnary RPAREN) )
	;

expressionFctParam
        : expression (COMMA expression)*
        ;

expressionTerm		
	: TEXT
	| expressionUnary
	| PROPERTY_NAME
	| DATE
	| DURATION_P
	| DURATION_T
	| NAME (LPAREN expressionFctParam? RPAREN)?
	| expressionGeometry
	| LPAREN expression RPAREN
	;
	
expression : expression MULT expression
           | expression UNARY expression
           | expressionTerm
           ;

filterGeometry
        : BBOX LPAREN expression COMMA expressionUnary COMMA expressionUnary COMMA expressionUnary COMMA expressionUnary (COMMA TEXT)? RPAREN
        | BEYOND LPAREN expression COMMA expression COMMA expression COMMA expression RPAREN
        | CONTAINS LPAREN expression COMMA expression RPAREN
        | CROSSES LPAREN expression COMMA expression RPAREN
        | DISJOINT LPAREN expression COMMA expression RPAREN
        | DWITHIN LPAREN expression COMMA expression COMMA expression COMMA expression RPAREN
        | EQUALS LPAREN expression COMMA expression RPAREN
        | INTERSECTS LPAREN expression COMMA expression RPAREN
        | OVERLAPS LPAREN expression COMMA expression RPAREN
        | TOUCHES LPAREN expression COMMA expression RPAREN
        | WITHIN LPAREN expression COMMA expression RPAREN
        ;

filterTerm 	: expression
                    (
                              COMPARE  expression
                            | NOT? IN LPAREN (expressionFctParam )?  RPAREN
                            | BETWEEN expression AND expression
                            | NOT? LIKE expression
                            | NOT? ILIKE expression
                            | IS NOT? NULL
                            | AFTER  expression
                            | ANYINTERACTS expression
                            | BEFORE expression
                            | BEGINS expression
                            | BEGUNBY expression
                            | DURING expression
                            | ENDEDBY expression
                            | ENDS expression
                            | MEETS expression
                            | METBY expression
                            | OVERLAPPEDBY expression
                            | TCONTAINS expression
                            | TEQUALS expression
                            | TOVERLAPS expression
                    ) 
                | filterGeometry
                ;

filter : filter (AND filter)+
       | filter (OR filter )+
       | LPAREN filter RPAREN
       | NOT (filterTerm | (LPAREN filter RPAREN) )
       | filterTerm
       ;

filterOrExpression : filter | expression ;


