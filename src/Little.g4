grammar Little;

// Token names/Lexer rules
tokens{PROGRAM,BEGIN,END,FUNCTION,READ,WRITE,IF,ELSE,ENDIF,WHILE,ENDWHILE,CONTINUE,BREAK,RETURN,INT,VOID,STRING,FLOAT}
COMMENT: '--' ~('\r' | '\n')* -> skip;
WS: [ \t\r\n]+ -> skip; // skip spaces, tabs, newlines
IDENTIFIER: [a-zA-Z0-9]+;
INTLITERAL: ;

// Parser rules
