import java.util.ArrayList;

public class IntermediateRepresentation {

    ArrayList<String> statements;

    public IntermediateRepresentation() {
        statements = new ArrayList<>();
        statements.add("LABEL  main");
    }

    public void addStatement(String stmt) {
        statements.add(stmt);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (String stmt : statements) {
            stringBuilder.append(stmt + "\n");
        }
        return stringBuilder.toString().trim();
    }
}
