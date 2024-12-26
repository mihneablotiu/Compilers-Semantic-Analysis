package cool.structures;

import java.util.LinkedHashMap;
import java.util.Map;

public class LetSymbol extends Symbol implements Scope {
    private final Map<String, Symbol> symbols = new LinkedHashMap<>();
    private final Scope parent;

    public LetSymbol(String name, Scope parent) {
        super(name);
        this.parent = parent;
    }

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

        if (parent != null)
            return parent.lookup(name);

        return null;
    }

    public Map<String, Symbol> getSymbols() {
        return symbols;
    }

    @Override
    public Scope getParent() {
        return parent;
    }


    @Override
    public String toString() {
        return symbols.values().toString();
    }
}
