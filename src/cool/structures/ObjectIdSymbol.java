package cool.structures;

public class ObjectIdSymbol extends Symbol {
    private TypeSymbol type;

    public ObjectIdSymbol(String name) {
        super(name);
    }

    public TypeSymbol getType() {
        return type;
    }

    public void setType(TypeSymbol type) {
        this.type = type;
    }
}
