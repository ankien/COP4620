import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.util.Scanner;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.Token;

public class Driver {
    public static void main(String[] args) throws Exception {
        /// Step 1: Scanner/Tokenizer/Lexer ///
        // Read the LITTLE file
        ANTLRInputStream input = new ANTLRInputStream(System.in);
        Little lexer = new Little(input);

        // Write to output file
        Token token = lexer.nextToken();
        while(token.getType() != Little.EOF) {
            System.out.println("Token Type: " + getTokenType(token.getType()) +
                    "\nValue: " + token.getText());
            token = lexer.nextToken();
        }
    }

    // Step 1 Helper(s)
    private static String getTokenType(int tokenType) {
        switch(tokenType) {
            case Little.KEYWORDS:
                return "KEYWORD";
            case Little.IDENTIFIER:
                return "IDENTIFIER";
            case Little.OPERATORS:
                return "OPERATOR";
            case Little.INTLITERAL:
                return "INTLITERAL";
            case Little.FLOATLITERAL:
                return "FLOATLITERAL";
            case Little.STRINGLITERAL:
                return "STRINGLITERAL";
            default:
                return "UNIDENTIFIED";
        }
    }
}
