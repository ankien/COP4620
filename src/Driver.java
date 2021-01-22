import java.io.File;
import java.io.FileInputStream;
import org.antlr.v4.runtime.ANTLRInputStream;

public class Driver {
    public static void main(String[] args) throws Exception {
        /// Step 1: Scanner/Tokenizer ///
        FileInputStream fileStream = new FileInputStream(new File(args[0]));
        ANTLRInputStream input = new ANTLRInputStream(fileStream);

    }
}
