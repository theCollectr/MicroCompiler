public class Symbol {
    private String type;
    private String name;
    private String value;

    public Symbol(String type, String name, String value) {
        this.type = type;
        this.name = name;
        this.value = value;
    }

    public Symbol(String type, String name) {
        this.type = type;
        this.name = name;
        this.value = null;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("name " + name);
        stringBuilder.append(" type " + type);
        if (value != null) {
            stringBuilder.append(" value " + value);
        }
        return stringBuilder.toString();
    }
}
