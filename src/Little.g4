lexer grammar Little;

// can add Parser rules


// Lexer rules
COMMENT: '--' ~[\r\n]* '\r'? '\n' -> skip;
WS: [ \t\r\n]+ -> skip;
KEYWORDS: ('PROGRAM'|'BEGIN'|'END'|'FUNCTION'|'READ'|'WRITE'|'IF'|'ELSE'|'ENDIF'|'WHILE'|'ENDWHILE'|'CONTINUE'|'BREAK'|'RETURN'|'INT'|'VOID'|'STRING'|'FLOAT');
IDENTIFIER: [a-zA-Z][a-zA-Z0-9]*;
OPERATORS: (':='|'+'|'-'|'*'|'/'|'='|'!='|'<'|'>'|'('|')'|';'|','|'<='|'>=');
INTLITERAL: [0-9]+;
FLOATLITERAL: [0-9]*'.'[0-9]+;
STRINGLITERAL: '"'~('"')*'"';