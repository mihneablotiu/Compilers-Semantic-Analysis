package cool.structures;

public class ObjectIdSymbol extends Symbol {
    private ClassSymbolWrapper typeSymbol;

    public ObjectIdSymbol(String name) {
        super(name);
    }

    public ClassSymbolWrapper getTypeSymbol() {
        return typeSymbol;
    }

    public void setTypeSymbol(ClassSymbolWrapper typeSymbol) {
        this.typeSymbol = typeSymbol;
    }
}
