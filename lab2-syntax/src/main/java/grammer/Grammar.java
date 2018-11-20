package grammer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

public class Grammar {
    public static final Symbol endSymbol = new Symbol("$", SymbolType.end),
            startSymbol = new Symbol("G'", SymbolType.start),
            emptySymbol = new Symbol("ε", SymbolType.empty);
    // TODO 还是assume喂入的grammar沒有ε，ε只是内部使用的，并且ε只有在消左递归的時候才会出現，也许lambda应该放到production裡面

    public static List<Production> all_productions = new ArrayList<>();
    // 以後，所有symbol都只是下面这个set裡面Symbol的reference
    // production同
    public static Set<Symbol> all_symbols = new HashSet<>();
    private static Set<String> user_specified_tokens = new HashSet<>();

    static {
        all_symbols.add(endSymbol);
        all_symbols.add(startSymbol);
    }

    public static void constructFromFile(File file) throws FileNotFoundException {
        Scanner scanner = new Scanner(file);

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.isEmpty()) continue;
            if ("%%".equals(line)) break;

            if (line.startsWith("%token")) {
                user_specified_tokens.add(line.split(" ")[1].trim());
            }
        }

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.isEmpty()) continue;
            List<String> temp = Arrays.asList(line.trim().split(" "));

            if (1 < temp.size() && ":".equals(temp.get(1))) {
                String head = temp.get(0);

                addProduction(head, temp.subList(2, temp.size()));

                while (scanner.hasNextLine() && !(line = scanner.nextLine()).trim().startsWith(";")) {
                    temp = Arrays.asList(line.trim().split(" "));
                    if ("|".equals(temp.get(0))) { // 检查嗎
                        addProduction(head, temp.subList(1, temp.size()));
                    }
                }
            }
        }
    }

    // first不会返回ε
    // TODO 如果出現左递归，无限循环
    public static Set<Symbol> first(List<Symbol> symbols) {
        Set<Symbol> result = new HashSet<>();

        if (symbols.isEmpty()) return result;
        // 这个是了为递归，其实如果有人一上來就传空list，好像不大好，但好像挺方便，比如下面follow那裡

        Symbol first_symbol = symbols.get(0);
        if (SymbolType.isTerminal(first_symbol.type)) {
            result.add(first_symbol);
            return result;
        }

        // if (SymbolType.nonterminal == first_symbol.type) // 黑默认一定是
        Set<Symbol> to_be_added = first(first_symbol);
        to_be_added.remove(emptySymbol);
        result.addAll(to_be_added);

        if (canDeriveEpsilon(first_symbol))
            result.addAll(first(symbols.subList(1, symbols.size())));

        return result;
    }

    public static Set<Symbol> first(Symbol symbol) {
        Set<Symbol> result = new HashSet<>();
        if (SymbolType.isTerminal(symbol.type)) {
            result.add(symbol);
            return result;
        }

        for (Production production : all_productions) {
            if (symbol == production.head) {
                Symbol first_symbol = production.body.get(0);
                if (SymbolType.ordinary_terminal == first_symbol.type) {
                    result.add(first_symbol);
                    continue;
                }

                // TODO 注：只是检查不是他自己还是防不胜防循环依赖的，但是應付四則运算应该够了，下面代码的重复也以後再说吧
                if (symbol != first_symbol) {
                    Set<Symbol> to_be_added = first(first_symbol);
                    to_be_added.remove(emptySymbol);
                    result.addAll(to_be_added);
                }

                if (canDeriveEpsilon(first_symbol)) {
                    result.addAll(first(production.body.subList(1, production.body.size())));
                }

//                result.addAll(first(production.body));
            }
        }
        return result;
    }

    // TODO 惊覺好像LR1不需要
    private static Set<Symbol> follow(Symbol symbol) {
        Set<Symbol> result = new HashSet<>();
        if (startSymbol == symbol) {
            result.add(endSymbol);
            return result;
        }

        for (Production production : all_productions) {
            int index = production.body.indexOf(symbol);

            if (0 <= index) {
                List<Symbol> after = production.body.subList(index, production.body.size());
                result.addAll(first(after));

                if (after.stream().allMatch(Grammar::canDeriveEpsilon)) {
                    result.addAll(follow(production.head));
                }
            }
        }
        return result;
    }

    /**
     * all private methods thereafter
     */
    private static boolean canDeriveEpsilon(Symbol symbol) {
        if (emptySymbol == symbol) return true;
        if (symbol.type == SymbolType.ordinary_terminal) return false;

        if (relevant_productions == null) {
            relevant_productions = all_productions.stream().filter(Grammar::isRelevant)
                    .collect(Collectors.toList());
        }

        return relevant_productions.stream()
                .filter(p -> symbol == p.head)
                .anyMatch(p -> p.body.stream().allMatch(Grammar::canDeriveEpsilon));
    }
    private static List<Production> relevant_productions;
    /*
    在canDeriveEpsilon裡，只需要考虑那些產生式body之中，沒有不是ε的终结符的，也沒有head的。
    如果產生式右边出現了自身，那么要么自身可以通過其他式子推出，
    要么自身不可以通过其他式子推出，不管怎么样这个式子都是可以不去管他的

    这樣也防止canDeriveEpsilon递归死
    */
    private static boolean isRelevant(Production production) {
        return production.body.stream()
                .noneMatch(s -> production.head == s || SymbolType.ordinary_terminal == s.type);
    }

    private static void addProduction(String head, List<String> body) {
        if (all_productions.isEmpty()) {

            all_productions.add(new Production(
                    startSymbol,
                    Arrays.asList(strToSymbol(head))
            ));
        }
        all_productions.add(new Production(
                strToSymbol(head),
                body.stream().map(Grammar::strToSymbol).collect(Collectors.toList())
        ));
    }

    // 保证symbol List的integrity
    private static Symbol strToSymbol(String str) {
        // 两种标识terminal的办法
        SymbolType type = SymbolType.nonterminal;
        if (user_specified_tokens.contains(str)) {
            type = SymbolType.ordinary_terminal;
        }
        if (str.startsWith("'") && str.endsWith("'")) {
            str = str.substring(1, str.length() - 1);
            type = SymbolType.ordinary_terminal;
        }

        for (Symbol symbol : all_symbols) {
            if (symbol.name.equals(str) && symbol.type == type) {
                // 所以穷极无聊之人可以用G', ε，$作为自己定义的nonterminal了嗎？
                return symbol;
            }
        }
        Symbol newSymbol = new Symbol(str, type);
        all_symbols.add(newSymbol);
        return newSymbol;
    }
}
