package grammer;

public class Symbol {
    public String name;
    public SymbolType type;

    Symbol(String name, SymbolType type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public String toString() {
        return name;
    }

    // equal就是reference的equal，不要override
}
