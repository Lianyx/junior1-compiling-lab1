package RE;

import util.Constants;

import java.util.*;

public class Regex implements Constants {
    private static final List<Character> afterEscape = Arrays.asList('n', 't', 'b', '\\', '[', ']', '{', '}', '|', '*', '(', ')');
    private static final List<Character> transform = Arrays.asList('\n', '\t', '\b', '\\', '[', ']', '{', '}', '|', '*', '(', ')');
    private static final List<Character> operators = Arrays.asList('|', '*', '(', ')');
    private static Map<Character, String> transformBeforeC = new HashMap<>();

    static {
        transformBeforeC.put('\n', "\\n");
        transformBeforeC.put('\t', "\\t");
        transformBeforeC.put('\b', "\\b");
    }

    public static List<RENode> toPostNotation(String regex) {
        List<RENode> reNodes = handleEscapeCharacter(regex);

        reNodes = addDot(reNodes);
        return toPostNotationAfterPreprocessed(reNodes);
    }

    public static String escape(char t) {
        if (transformBeforeC.containsKey(t)) {
            return transformBeforeC.get(t);
        }
        return "" + t;
    }

    /**
     * all private methods thereafter
     */
    private static List<RENode> toPostNotationAfterPreprocessed(List<RENode> regex) {
        Deque<RENode> symbols = new LinkedList<>();
        List<RENode> result = new ArrayList<>();

        for (RENode reNode : regex) {
            if (RENode.Type.OP == reNode.type) {
                char c = reNode.ch;
                switch (c) {
                    case Constants.DOT:
                    case Constants.VERTICAL_LINE:
                        if (!symbols.isEmpty()) {
                            RENode former = symbols.peek();
                            if (Constants.L_PARA != former.ch && comparePrecedence(former.ch, c) >= 0) {
                                symbols.pop();
                                result.add(former);
                            }
                        }
                        symbols.push(reNode);
                        break;
                    case Constants.L_PARA:
                        symbols.push(reNode);
                        break;
                    case Constants.R_PARA:
                        while (Constants.L_PARA != (reNode = symbols.pop()).ch) {
                            result.add(reNode);
                        }
                        break;
                    case Constants.ASTERISK:
                        result.add(reNode);
                        break;
                }
            } else {
                result.add(reNode);
            }
        }

        while (!symbols.isEmpty()) {
            result.add(symbols.pop());
        }

        return result;
    }

    private static List<RENode> handleEscapeCharacter(String regex) {
        List<RENode> result = new ArrayList<>(); // LinkedList沒有next...

        for (int i = 0; i < regex.length(); i++) {
            char c = regex.charAt(i);
            if ('\\' == c) {
                char c1 = regex.charAt(++i);
                int index;
                if (-1 != (index = afterEscape.indexOf(c1))) {
                    result.add(new RENode(transform.get(index), RENode.Type.CH));
                } else {
                    // report error
                }
            } else if (operators.contains(c)) {
                result.add(new RENode(c, RENode.Type.OP));
            } else {
                result.add(new RENode(c, RENode.Type.CH));
            }
        }
        return result;
    }

//    private static List<RENode> otherSymbol(List<RENode> regex) {
//        for (int i = 0; i < regex.size(); i++) {
//            RENode reNode = regex.get(i);
//            if ('[' == reNode.ch) {
//                regex.remove(i);
//                regex.add(i, new RENode())
//            }
//        }
//    }

    private static List<RENode> addDot(List<RENode> regex) {
        int ptr = 0;
        List<RENode> result = new ArrayList<>();

        while (ptr < regex.size() - 1) {
            result.add(regex.get(ptr));
            if (shouldAddDotBetween(regex.get(ptr), regex.get(ptr + 1))) {
                result.add(new RENode(Constants.DOT, RENode.Type.OP));
            }
            ptr++;
        }
        result.add(regex.get(ptr));
        return result;
    }

    private static boolean shouldAddDotBetween(RENode left, RENode right) {
        return (Constants.R_PARA == left.ch && RENode.Type.OP == left.type
                || Constants.ASTERISK == left.ch && RENode.Type.OP == left.type
                || RENode.Type.CH == left.type)
                &&
                (Constants.L_PARA == right.ch && RENode.Type.OP == right.type
                        || RENode.Type.CH == right.type);
    }

    // 只比较dot和vertical line，因为asterisk是一元运算符
    private static int comparePrecedence(char former, char latter) {
        if (former == latter) {
            return 0;
        }

        if (Constants.DOT == former) {
            return 1;
        }

        return -1;
    }

    public static void main(String[] args) {
        System.out.println();
        System.out.println(toPostNotation("(a|b)*abb"));
    }
}
