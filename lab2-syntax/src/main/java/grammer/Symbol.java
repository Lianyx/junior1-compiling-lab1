package grammer;

public class Symbol {
    public String lexeme;
    public SymbolType type;

    Symbol(String lexeme, SymbolType type) {
        this.lexeme = lexeme;
        this.type = type;
    }

    @Override
    public String toString() {
        return lexeme;
    }

    // equal就是reference的equal，不要override
}
