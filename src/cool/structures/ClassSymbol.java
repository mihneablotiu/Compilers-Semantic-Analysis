package cool.structures;

import java.util.LinkedHashMap;
import java.util.Map;

public class ClassSymbol extends Symbol implements Scope {
    private final Map<String, Symbol> fieldSymbols = new LinkedHashMap<>();
    private final Map<String, Symbol> methodSymbols = new LinkedHashMap<>();
    private final Scope parent = SymbolTable.globals;
    private Scope directParent = null;

    public ClassSymbol(String name) {
        super(name);
    }

    public boolean addField(Symbol sym) {
        // Reject duplicates in the same scope.
        if (fieldSymbols.containsKey(sym.getName()))
            return false;

        fieldSymbols.put(sym.getName(), sym);

        return true;
    }

    public boolean addMethod(Symbol sym) {
        // Reject duplicates in the same scope.
        if (methodSymbols.containsKey(sym.getName()))
            return false;

        methodSymbols.put(sym.getName(), sym);

        return true;
    }

    public Symbol lookupField(String name) {
        var sym = fieldSymbols.get(name);

        if (sym != null)
            return sym;

        if (directParent != null && directParent != SymbolTable.globals)
            return ((ClassSymbol) directParent).lookupField(name);

        return null;
    }

    public Symbol lookupMethod(String name) {
        var sym = methodSymbols.get(name);

        if (sym != null)
            return sym;

        if (directParent != null && directParent != SymbolTable.globals)
            return ((ClassSymbol) directParent).lookupMethod(name);

        return null;
    }

    public boolean isChildOf(ClassSymbol parent) {
        if (parent.name.equals("Object"))
            return true;

        if (this.name.equals(parent.name))
            return true;

        if (this.directParent == SymbolTable.globals || this.directParent == null)
            return false;

        return ((ClassSymbol) this.directParent).isChildOf(parent);
    }

    public int countParents() {
        if (this.name.equals("Object"))
            return 0;

        return 1 + ((ClassSymbol) this.directParent).countParents();
    }

    public ClassSymbol leastUpperBound(ClassSymbol other) {
        if (this == other)
            return this;

        if (this.isChildOf(other))
            return other;

        if (other.isChildOf(this))
            return this;

        return ((ClassSymbol) this.directParent).leastUpperBound(other);
    }

    public boolean isInCycle(ClassSymbol initialClass) {
        if (this.name.equals("Object") || this.directParent == null)
            return false;

        if (this == initialClass)
            return true;

        return ((ClassSymbol) this.directParent).isInCycle(initialClass);
    }

    @Override
    public boolean add(Symbol sym) {
        return false;
    }

    @Override
    public Symbol lookup(String str, boolean isField) {
        if (isField)
            return lookupField(str);
        else
            return lookupMethod(str);
    }

    @Override
    public Scope getParent() {
        return parent;
    }

    public Map<String, Symbol> getFieldSymbols() {
        return fieldSymbols;
    }

    public Map<String, Symbol> getMethodSymbols() {
        return methodSymbols;
    }

    public Scope getDirectParent() {
        return directParent;
    }

    public void setDirectParent(Scope directParent) {
        this.directParent = directParent;
    }
}
