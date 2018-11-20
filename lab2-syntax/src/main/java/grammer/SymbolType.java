package grammer;

// TODO 感覺应该用不同的子类來代替enum的
public enum SymbolType {
    ordinary_terminal, nonterminal, end, start, empty;
    // 正常production.body中只会有nonterminal和terminal，如果是LL1，可能会有empty

    public static boolean isTerminal(SymbolType type) {
        return ordinary_terminal == type
                || end == type;
        // TODO empty算不算啊
    }
}
