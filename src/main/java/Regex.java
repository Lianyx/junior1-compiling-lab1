import java.util.Deque;
import java.util.LinkedList;

public class Regex implements Constants {
    public static String toPostNotation(String regex) {

        regex = addDot(regex);
        return toPostNotationAfterPreprocessed(regex);
    }

    /**
     * all private methods thereafter
     * */
    private static String toPostNotationAfterPreprocessed(String regex) {
        Deque<Character> symbols = new LinkedList<>();
        StringBuilder letters = new StringBuilder();

        for (int i = 0; i < regex.length(); i++) {
            char c = regex.charAt(i);
            switch (c) {
                case DOT:
                case VERTICAL_LINE:
                    if (!symbols.isEmpty()) {
                        char former = symbols.peek();
                        if (L_PARA != former && comparePrecedence(former, c) >= 0) {
                            symbols.pop();
                            letters.append(former);
                        }
                    }
                    symbols.push(c);
                    break;
                case L_PARA:
                    symbols.push(c);
                    break;
                case R_PARA:
                    while (L_PARA != (c = symbols.pop())) {
                        letters.append(c);
                    }
                    break;
                case ASTERISK:
                    letters.append(c);
                    break;
                default: // c from Σ or c == ε
                    letters.append(c);
                    break;
            }
        }

        while (!symbols.isEmpty()) {
            letters.append(symbols.pop());
        }

        return letters.toString();
    }

    private static String handleEscapeCharacter(String regex) {
        return regex;
    }

    private static String addDot(String regex) {
        int ptr = 0;
        StringBuilder sb = new StringBuilder();
        while (ptr < regex.length() - 1) {
            sb.append(regex.charAt(ptr));
            if (shouldAddDotBetween(regex.charAt(ptr), regex.charAt(ptr + 1))) {
                sb.append(DOT);
            }
            ptr++;
        }
        sb.append(regex.charAt(ptr));
        return sb.toString();
    }

    private static boolean shouldAddDotBetween(char left, char right) {
        return (R_PARA == left || ASTERISK == left || isCharacterOrDigit(left))
                &&
                (L_PARA == right || isCharacterOrDigit(right));
    }

    private static boolean isCharacterOrDigit(char c) {
        return 'a' <= c && c <= 'z' || 'A' <= c && c <= 'Z' || '0' <= c && c <= '9';
    }

    // 只比较dot和vertical line，因为asterisk是一元运算符
    private static int comparePrecedence(char former, char latter) {
        if (former == latter) {
            return 0;
        }

        if (DOT == former) {
            return 1;
        }

        return -1;
    }
}
