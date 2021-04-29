

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.io.IOException;

public class Driver {
    public static void main(String[] args) {
        MicroLexer lexer = null;
        String srcFile = args[0];
        try {
            CharStream charStream = CharStreams.fromFileName(srcFile);
            lexer = new MicroLexer(charStream);
        } catch (IOException e) {
            System.out.println("Error: incorrect input file");
            System.exit(0);
        }

        MicroParser parser = new MicroParser(new CommonTokenStream(lexer));
        parser.program();

        if (parser.getNumberOfSyntaxErrors() == 0) {
            System.out.println("accepted");
        } else {
            System.out.println("not accepted");
        }
    }
}
