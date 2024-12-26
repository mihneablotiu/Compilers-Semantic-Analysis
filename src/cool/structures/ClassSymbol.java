package cool.structures;

import java.util.LinkedHashMap;
import java.util.Map;

public class ClassSymbol extends TypeSymbol implements Scope {
    private final Map<String, Symbol> symbols = new LinkedHashMap<>();
    private final Scope parent;
    private Scope directParent;

    public ClassSymbol(String name, Scope parent) {
        super(name);
        this.parent = parent;
    }

    public boolean isInCycle(String initialName) {
        if (this.getName().equals("Object")) {
            return false;
        }

        if (this.getName().equals(initialName)) {
            return true;
        }

        return ((ClassSymbol) this.directParent).isInCycle(initialName);
    }

    @Override
    public boolean add(Symbol sym) {
        // Reject duplicates in the same scope.
        if (symbols.containsKey(sym.getName()))
            return false;

        symbols.put(sym.getName(), sym);

        return true;
    }

    @Override
    public Symbol lookup(String name) {
        var sym = symbols.get(name);

        if (sym != null)
            return sym;

        if (directParent != null)
            return directParent.lookup(name);

        return null;
    }

    @Override
    public Scope getParent() {
        return parent;
    }

    @Override
    public String toString() {
        return symbols.values().toString();
    }

    public Scope getDirectParent() {
        return directParent;
    }

    public void setDirectParent(Scope directParent) {
        this.directParent = directParent;
    }
}
