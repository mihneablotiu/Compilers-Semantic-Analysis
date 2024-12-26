package cool.structures;

public class FieldSymbol extends Symbol {
    private ClassSymbolWrapper typeSymbol;

    public FieldSymbol(String name) {
        super(name);
    }

    public FieldSymbol(String name, ClassSymbolWrapper typeSymbol) {
        super(name);
        this.typeSymbol = typeSymbol;
    }

    public ClassSymbolWrapper getTypeSymbol() {
        return typeSymbol;
    }

    public void setTypeSymbol(ClassSymbolWrapper typeSymbol) {
        this.typeSymbol = typeSymbol;
    }
}
