import java.util.ArrayList;

public class SymbolTable {
    private ArrayList<Scope> scopes;
    private ArrayList<Scope> activeScopes;
    private int counter = 0;

    public SymbolTable() {
        scopes = new ArrayList<>();
        activeScopes = new ArrayList<>();
        Scope globalScope = new Scope("Global");
        scopes.add(globalScope);
        activeScopes.add(globalScope);
    }

    public void openScope(String name) {
        Scope scope = new Scope(name);
        scopes.add(scope);
        activeScopes.add(scope);
    }

    public void openScope() {
        String scopeName = "BLOCK #" + (++counter);
        Scope scope = new Scope(scopeName);
        scopes.add(scope);
        activeScopes.add(scope);
    }

    public void closeScope() {
        activeScopes.remove(activeScopes.size() - 1);
    }

    public void addSymbol(String type, String name, String value) {
        Scope currentScope = activeScopes.get(activeScopes.size() - 1);
        currentScope.addSymbol(type, name, value);
    }

    public void addSymbol(String type, String name) {
        Scope currentScope = activeScopes.get(activeScopes.size() - 1);
        currentScope.addSymbol(type, name);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Scope scope : scopes) {
            stringBuilder.append(scope.toString() + "\n");
        }
        return stringBuilder.toString().trim();
    }
}
