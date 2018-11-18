import RE.RENode;
import RE.Regex;
import util.Util;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    private static List<String> regexes = new ArrayList<>(),
            actions = new ArrayList<>();
    private static List<Integer> NFA_end_states = new ArrayList<>();

    private static final String outputBeforeSwitchCase = "" +
            "#include <stdio.h>\n" +
            "#include <string.h>\n" +
            "\n" +
            "char input[1000000];\n" +
            "int yylval = 0;\n" +
            "int ptr = 0;\n" +
            "int state = 0;\n" +
            "\n" +
            "int yylex() {\n" +
            "    int begin = 0;" +
            "    while (1) {\n" +
            "        char c = input[ptr++];\n" +
            "        switch (state) {\n";
    private static final String outputAfterSwitchCase = "" +
            "            default:\n" +
            "                break;\n" +
            "        }\n" +
            "    }\n" +
            "    return -1;\n" +
            "}\n" +
            "\n" +
            "int main() {\n" +
            "    char ch, file_name[25];\n" +
            "    int len = 0;\n" +
            "    FILE *fp;\n" +
            "\n" +
            "    printf(\"Enter name of a file you wish to see\\n\");\n" +
            "    scanf(\"%s\", file_name);\n" +
            "\n" +
            "    fp = fopen(file_name, \"r\"); // read mode\n" +
            "    while((ch = (char) fgetc(fp)) != EOF) {\n" +
            "        input[len++] = ch;\n" +
            "    }\n" +
            "    input[len] = EOF;\n" +
            "\n" +
            "    yylex();\n" +
            "}";
    private static final String error = "printf(\"Warning: an error occurs!!!\\n\");";

    /**
     * @param states 一组nfa_states的编号
     * @return 如果不是endState，返回-1，否则返回在regexes以及actions裡對應的index
     */
    private static int lineNo(Set<Integer> states) {
        boolean isEndState = false;
        int min = regexes.size() + 2;
        for (Integer i : states) {
            int index = NFA_end_states.indexOf(i);
            if (index != -1) {
                isEndState = true;
                min = Math.min(index, min); // 优先选择寫在上面的式子
            }
        }
        return isEndState ? min : -1;
    }

    public static void main(String[] args) {
        try {
            // input
            Scanner scanner = new Scanner(new File("./src/main/resources/test.l"));
            Map<String, String> reDef = new HashMap<>();

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.isEmpty()) continue;
                if ("%%".equals(line)) break;

                String[] split = line.split(" ");
                reDef.put(split[0], split[1].trim());
            }

            while (scanner.hasNextLine()) { // 先不支持分行寫，也不管{{}}這種
                String line = scanner.nextLine();
                if (line.isEmpty()) continue; // 先不管引号，也不管{digits這类}

                int start = 1, end = line.indexOf("\"", 1);
                String[] regex = {line.substring(start, end)};
                reDef.forEach((from, to) -> {
                    from = "\\{" + from + "\\}";
                    to = '(' + to + ')';
                    regex[0] = regex[0].replaceAll(from, to);
                });
                regexes.add(regex[0]);

                line = line.substring(end);
                start = line.indexOf("{") + 1;
                end = line.indexOf("}");
                String action = line.substring(start, end);
                actions.add(action);
            }

            // process
            List<NFA> NFAs = regexes.stream().map(r -> {
                List<RENode> postRE = Regex.toPostNotation(r);
                return NFA.postRegexToNFA(postRE);
            }).collect(Collectors.toList());

            NFA nfa = NFA.combine(NFAs, NFA_end_states);
            DFA dfa = DFA.NFA2DFA(nfa);

            List<Integer> d1 = new ArrayList<>(), d2 = new ArrayList<>();
            for (int i = 1; i <= dfa.NFA_states.size(); i++) {
                if (-1 == lineNo(dfa.NFA_states.get(i))) {
                    d1.add(i);
                } else {
                    d2.add(i);
                }
            }

            dfa = DFA.minimizeState(dfa, Arrays.asList(d1, d2));

            // output
            StringBuilder output = new StringBuilder(outputBeforeSwitchCase);
            for (int i = 0; i < dfa.table.size(); i++) {
                StringBuilder thisCase = new StringBuilder("case ");
                thisCase.append(i);
                thisCase.append(": \n");

                Map<Character, Integer> edges = dfa.table.get(i);
                if (!edges.isEmpty()) {
                    boolean[] first = {true};
                    edges.forEach((c, next_i) -> {
                        if (first[0]) {
                            first[0] = false;
                        } else {
                            thisCase.append("else ");
                        }

                        thisCase.append("if (\'").append(Regex.escape(c)).append("\' == c) { state = ")
                                .append(next_i).append("; }\n");
                    });
                    thisCase.append("else ");
                }
                Set<Integer> nfa_states = dfa.NFA_states.get(i);
                int lineNo = lineNo(nfa_states);
                if (-1 == lineNo) {
                    thisCase.append(" { " + error);
                    thisCase.append(" return -1; } \n");
                } else {
                    thisCase.append("{ ptr--; char lexeme[25]; memcpy(lexeme, &input[begin], ptr - begin); lexeme[ptr-begin] = 0; begin = ptr; ");
                    thisCase.append(actions.get(lineNo));
                    thisCase.append(" state = 0;");
                    thisCase.append(" if (EOF == c) return 1; } \n");
                }
                thisCase.append("break;\n");

                output.append(thisCase.toString()); // 那还要兩個Stringbuilder干什么…
            }
            output.append(outputAfterSwitchCase);

            File outputFile = new File("./lyx.ee.c");
            if (outputFile.createNewFile()) {
                System.out.println("file \"lyx.ee.c\" already exists");
            }

            PrintWriter printWriter = new PrintWriter(outputFile);
            printWriter.println(output.toString());
            printWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
