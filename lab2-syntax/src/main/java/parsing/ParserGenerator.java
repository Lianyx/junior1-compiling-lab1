package parsing;

import LR.LR1;
import LR.State;
import grammer.SymbolType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static grammer.Grammar.startSymbol;

public class ParserGenerator {
    private static final String s0 = "" +
            "import java.io.IOException;\n" +
            "import java.nio.charset.StandardCharsets;\n" +
            "import java.nio.file.Files;\n" +
            "import java.nio.file.Paths;\n" +
            "import java.util.*;\n" +
            "\n" +
            "public class Tab {\n" +
            "    private static Deque<Integer> state_No_stack = new LinkedList<>();\n" +
            "\n" +
            "    private static void parse(List<String> input) {\n" +
            "        int ptr = 0;\n" +
            "        state_No_stack.push(0);\n" +
            "\n" +
            "        while (true) {\n" +
            "            int No = state_No_stack.peek();\n" +
            "            String chr = input.get(ptr);\n" +
            "            switch (No) {\n",

    s1 = "" +
            "                default:\n" +
            "                    System.out.println(\"!!! wrong\");\n" +
            "                    return;\n" +
            "            }\n" +
            "        }\n" +
            "    }\n" +
            "\n" +
            "    private static int GOTO(int state, String nonterminal) {\n" +
            "        switch (state) {\n",

    s2 = "" +
            "            default:\n" +
            "                return -1;\n" +
            "        }\n" +
            "    }\n" +
            "\n" +
            "    public static void main(String[] args) throws IOException {\n" +
            "        String text = new String(Files.readAllBytes(Paths.get(\"./src/to_be_parsed1.txt\")), StandardCharsets.UTF_8);\n" +
            "        List<String> input = new ArrayList<>(Arrays.asList(text.split(\"[ \\t\\n]\")));\n" +
            "        input.add(\"$\");\n" +
            "        parse(input);\n" +
            "    }\n" +
            "}\n";

    // TODO 不支持$…
    public static void generateParser() throws IOException {
        StringBuilder sb = new StringBuilder(s0);

        for (int i = 0; i < LR1.LR_automation.size(); i++) {
            State state = LR1.LR_automation.get(i);

            String S_case = state.next_states.entrySet().stream()
                    .filter(e -> SymbolType.isTerminal(e.getKey().type))
                    .map(e -> String.format("if (\"%s\".equals(chr)) {\n" +
                            "ptr++;\n" +
                            "state_No_stack.push(%d);\n" +
                            "}", e.getKey().name, LR1.getStateNo(e.getValue())))
                    .reduce((a, b) -> a + " else " + b)
                    .orElse("");

            String R_case = state.items.stream()
                    .filter(itm -> itm.isEnd() && itm.production.head != startSymbol)
                    .map(itm -> String.format("if (%s) {\n" +
                                    "for (int i = 0; i < %d; i++) {\n" +
                                    "state_No_stack.pop();\n" +
                                    "}\n" +
                                    "System.out.println(\"%s\");\n" +
                                    "state_No_stack.push(GOTO(state_No_stack.peek(), \"%s\"));\n" +
                                    "}",
                            itm.afters.stream().map(
                                    s -> String.format("\"%s\".equals(chr)", s.name)
                            ).reduce((a, b) -> a + " || " + b).orElse(""), // 不可能走到orElse
                            itm.production.body.size(),
                            itm.production.toString(),
                            itm.production.head.name))
                    .reduce((a, b) -> a + " else " + b)
                    .orElse("");

            String accept_case = state.items.stream()
                    .filter(itm -> itm.isEnd() && itm.production.head == startSymbol)
                    .map(itm -> "if (\"$\".equals(chr)) {\n" +
                            "System.out.println(\"Accept\");\n" +
                            "return;\n" +
                            "}")
                    .findFirst().orElse(""); // 這裡的orElse不可能

            if (S_case.isEmpty() && R_case.isEmpty() && accept_case.isEmpty()) {
                continue;
            }

            // 开始拼
            sb.append(String.format("case %d:\n", i));

            sb.append(S_case);
            if (!S_case.isEmpty() && !(R_case.isEmpty() && accept_case.isEmpty())) {
                sb.append(" else ");
            }
            sb.append(R_case);
            if (!R_case.isEmpty() && !accept_case.isEmpty()) {
                sb.append(" else ");
            }
            sb.append(accept_case);

            sb.append("else {\n" +
                    "System.out.println(\"!!! wrong\");\n" +
                    "return;\n" +
                    "}\n");

            // 如果只有accept的情况，兩条分支都有return，所以case的最後就不再需要break了
            if (!R_case.isEmpty() || !S_case.isEmpty()) {
                sb.append("break;\n");
            }
        }

        sb.append(s1);

        for (int i = 0; i < LR1.LR_automation.size(); i++) {
            State state = LR1.LR_automation.get(i);

            String case_i = state.next_states.entrySet().stream()
                    .filter(e -> SymbolType.nonterminal == e.getKey().type)
                    .map(e -> String.format("if (\"%s\".equals(nonterminal)) {\n" +
                            "return %d;\n" +
                            "}\n", e.getKey().name, LR1.getStateNo(e.getValue())))
                    .reduce((a, b) -> a + b)
                    .orElse("");

            if (case_i.isEmpty()) {
                continue;
            }
            sb.append(String.format("case %d:\n", i));
            sb.append(case_i)
                    .append("return -1;\n");
        }

        sb.append(s2);

        // 往文件裡寫
        Path path = Paths.get("./src/main/java/Tab.java");
        Files.write(path, sb.toString().getBytes());
    }
}
