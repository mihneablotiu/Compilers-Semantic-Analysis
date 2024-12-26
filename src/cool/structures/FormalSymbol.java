package cool.structures;

public class FormalSymbol extends Symbol {
    private ClassSymbolWrapper typeSymbol;

    public FormalSymbol(String name) {
        super(name);
    }

    public ClassSymbolWrapper getTypeSymbol() {
        return typeSymbol;
    }

    public void setTypeSymbol(ClassSymbolWrapper typeSymbol) {
        this.typeSymbol = typeSymbol;
    }
}
