

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

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
        ParseTree tree = parser.program();
        SymbolTableVisitor symbolTableVisitor = new SymbolTableVisitor();
        symbolTableVisitor.visit(tree);

        IRVisitor irVisitor = new IRVisitor(symbolTableVisitor.getSymbolTable());
        irVisitor.visit(tree);
        System.out.print(irVisitor.getIntermediateRepresentation());
    }
}
