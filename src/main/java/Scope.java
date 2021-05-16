import java.util.ArrayList;

public class Scope {
    private String name;
    private ArrayList<Symbol> symbols;

    public Scope(String name) {
        this.name = name;
        symbols = new ArrayList<>();
    }

    public void addSymbol(String type, String name, String value) {
        Symbol symbol = new Symbol(type, name, value);
        symbols.add(symbol);
    }

    public void addSymbol(String type, String name) {
        Symbol symbol = new Symbol(type, name);
        symbols.add(symbol);
    }

    public String getType(String name) {
        for (Symbol symbol : symbols) {
            if (symbol.getName().equals(name)) {
                return symbol.getType();
            }
        }
        return null;
    }

    public Symbol getSymbol(String name) {
        for (Symbol symbol : symbols) {
            if (symbol.getName().equals(name)) {
                return symbol;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<<Scope " + name + ">>\n");
        for (Symbol symbol : symbols) {
            stringBuilder.append(symbol.toString() + "\n");
        }
        return stringBuilder.toString();
    }
}
