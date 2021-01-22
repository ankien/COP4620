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
        Scanner scanner = new Scanner(System.in);
        scanner.next();
        String inputFilename = "inputs/" + scanner.next() + ".micro";
        FileInputStream fileStream = new FileInputStream(new File(inputFilename));
        ANTLRInputStream input = new ANTLRInputStream(fileStream);
        Little lexer = new Little(input);

        // Create output file
        PrintWriter file = new PrintWriter(inputFilename.substring(0,inputFilename.lastIndexOf('.')) + ".out");

        // Write to output file
        Token token = lexer.nextToken();
        while(token.getType() != Little.EOF) {
            file.println("Token Type: " + getTokenType(token.getType()) +
                        "\nValue: " + token.getText());
            token = lexer.nextToken();
        }

        file.close();
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
