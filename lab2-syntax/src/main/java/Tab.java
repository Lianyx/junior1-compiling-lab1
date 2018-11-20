import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Tab {
    private static Deque<Integer> state_No_stack = new LinkedList<>();

    private static void parse(List<String> input) {
        int ptr = 0;
        state_No_stack.push(0);

        while (true) {
            int No = state_No_stack.peek();
            String chr = input.get(ptr);
            switch (No) {
case 0:
if ("(".equals(chr)) {
ptr++;
state_No_stack.push(4);
} else if ("ID".equals(chr)) {
ptr++;
state_No_stack.push(5);
}else {
System.out.println("!!! wrong");
return;
}
break;
case 1:
if ("+".equals(chr)) {
ptr++;
state_No_stack.push(6);
} else if ("-".equals(chr)) {
ptr++;
state_No_stack.push(7);
} else if ("$".equals(chr)) {
System.out.println("Accept");
return;
}else {
System.out.println("!!! wrong");
return;
}
break;
case 2:
if ("/".equals(chr)) {
ptr++;
state_No_stack.push(9);
} else if ("*".equals(chr)) {
ptr++;
state_No_stack.push(8);
} else if ("+".equals(chr) || "$".equals(chr) || "-".equals(chr)) {
for (int i = 0; i < 1; i++) {
state_No_stack.pop();
}
System.out.println("expr -> term");
state_No_stack.push(GOTO(state_No_stack.peek(), "expr"));
}else {
System.out.println("!!! wrong");
return;
}
break;
case 3:
if ("+".equals(chr) || "$".equals(chr) || "/".equals(chr) || "-".equals(chr) || "*".equals(chr)) {
for (int i = 0; i < 1; i++) {
state_No_stack.pop();
}
System.out.println("term -> factor");
state_No_stack.push(GOTO(state_No_stack.peek(), "term"));
}else {
System.out.println("!!! wrong");
return;
}
break;
case 4:
if ("(".equals(chr)) {
ptr++;
state_No_stack.push(13);
} else if ("ID".equals(chr)) {
ptr++;
state_No_stack.push(14);
}else {
System.out.println("!!! wrong");
return;
}
break;
case 5:
if ("+".equals(chr) || "$".equals(chr) || "/".equals(chr) || "-".equals(chr) || "*".equals(chr)) {
for (int i = 0; i < 1; i++) {
state_No_stack.pop();
}
System.out.println("factor -> ID");
state_No_stack.push(GOTO(state_No_stack.peek(), "factor"));
}else {
System.out.println("!!! wrong");
return;
}
break;
case 6:
if ("(".equals(chr)) {
ptr++;
state_No_stack.push(4);
} else if ("ID".equals(chr)) {
ptr++;
state_No_stack.push(5);
}else {
System.out.println("!!! wrong");
return;
}
break;
case 7:
if ("(".equals(chr)) {
ptr++;
state_No_stack.push(4);
} else if ("ID".equals(chr)) {
ptr++;
state_No_stack.push(5);
}else {
System.out.println("!!! wrong");
return;
}
break;
case 8:
if ("(".equals(chr)) {
ptr++;
state_No_stack.push(4);
} else if ("ID".equals(chr)) {
ptr++;
state_No_stack.push(5);
}else {
System.out.println("!!! wrong");
return;
}
break;
case 9:
if ("(".equals(chr)) {
ptr++;
state_No_stack.push(4);
} else if ("ID".equals(chr)) {
ptr++;
state_No_stack.push(5);
}else {
System.out.println("!!! wrong");
return;
}
break;
case 10:
if ("+".equals(chr)) {
ptr++;
state_No_stack.push(20);
} else if (")".equals(chr)) {
ptr++;
state_No_stack.push(19);
} else if ("-".equals(chr)) {
ptr++;
state_No_stack.push(21);
}else {
System.out.println("!!! wrong");
return;
}
break;
case 11:
if ("/".equals(chr)) {
ptr++;
state_No_stack.push(23);
} else if ("*".equals(chr)) {
ptr++;
state_No_stack.push(22);
} else if ("+".equals(chr) || ")".equals(chr) || "-".equals(chr)) {
for (int i = 0; i < 1; i++) {
state_No_stack.pop();
}
System.out.println("expr -> term");
state_No_stack.push(GOTO(state_No_stack.peek(), "expr"));
}else {
System.out.println("!!! wrong");
return;
}
break;
case 12:
if ("+".equals(chr) || ")".equals(chr) || "/".equals(chr) || "-".equals(chr) || "*".equals(chr)) {
for (int i = 0; i < 1; i++) {
state_No_stack.pop();
}
System.out.println("term -> factor");
state_No_stack.push(GOTO(state_No_stack.peek(), "term"));
}else {
System.out.println("!!! wrong");
return;
}
break;
case 13:
if ("(".equals(chr)) {
ptr++;
state_No_stack.push(13);
} else if ("ID".equals(chr)) {
ptr++;
state_No_stack.push(14);
}else {
System.out.println("!!! wrong");
return;
}
break;
case 14:
if ("+".equals(chr) || ")".equals(chr) || "/".equals(chr) || "-".equals(chr) || "*".equals(chr)) {
for (int i = 0; i < 1; i++) {
state_No_stack.pop();
}
System.out.println("factor -> ID");
state_No_stack.push(GOTO(state_No_stack.peek(), "factor"));
}else {
System.out.println("!!! wrong");
return;
}
break;
case 15:
if ("/".equals(chr)) {
ptr++;
state_No_stack.push(9);
} else if ("*".equals(chr)) {
ptr++;
state_No_stack.push(8);
} else if ("+".equals(chr) || "$".equals(chr) || "-".equals(chr)) {
for (int i = 0; i < 3; i++) {
state_No_stack.pop();
}
System.out.println("expr -> expr + term");
state_No_stack.push(GOTO(state_No_stack.peek(), "expr"));
}else {
System.out.println("!!! wrong");
return;
}
break;
case 16:
if ("/".equals(chr)) {
ptr++;
state_No_stack.push(9);
} else if ("*".equals(chr)) {
ptr++;
state_No_stack.push(8);
} else if ("+".equals(chr) || "$".equals(chr) || "-".equals(chr)) {
for (int i = 0; i < 3; i++) {
state_No_stack.pop();
}
System.out.println("expr -> expr - term");
state_No_stack.push(GOTO(state_No_stack.peek(), "expr"));
}else {
System.out.println("!!! wrong");
return;
}
break;
case 17:
if ("+".equals(chr) || "$".equals(chr) || "/".equals(chr) || "-".equals(chr) || "*".equals(chr)) {
for (int i = 0; i < 3; i++) {
state_No_stack.pop();
}
System.out.println("term -> term * factor");
state_No_stack.push(GOTO(state_No_stack.peek(), "term"));
}else {
System.out.println("!!! wrong");
return;
}
break;
case 18:
if ("+".equals(chr) || "$".equals(chr) || "/".equals(chr) || "-".equals(chr) || "*".equals(chr)) {
for (int i = 0; i < 3; i++) {
state_No_stack.pop();
}
System.out.println("term -> term / factor");
state_No_stack.push(GOTO(state_No_stack.peek(), "term"));
}else {
System.out.println("!!! wrong");
return;
}
break;
case 19:
if ("+".equals(chr) || "$".equals(chr) || "/".equals(chr) || "-".equals(chr) || "*".equals(chr)) {
for (int i = 0; i < 3; i++) {
state_No_stack.pop();
}
System.out.println("factor -> ( expr )");
state_No_stack.push(GOTO(state_No_stack.peek(), "factor"));
}else {
System.out.println("!!! wrong");
return;
}
break;
case 20:
if ("(".equals(chr)) {
ptr++;
state_No_stack.push(13);
} else if ("ID".equals(chr)) {
ptr++;
state_No_stack.push(14);
}else {
System.out.println("!!! wrong");
return;
}
break;
case 21:
if ("(".equals(chr)) {
ptr++;
state_No_stack.push(13);
} else if ("ID".equals(chr)) {
ptr++;
state_No_stack.push(14);
}else {
System.out.println("!!! wrong");
return;
}
break;
case 22:
if ("(".equals(chr)) {
ptr++;
state_No_stack.push(13);
} else if ("ID".equals(chr)) {
ptr++;
state_No_stack.push(14);
}else {
System.out.println("!!! wrong");
return;
}
break;
case 23:
if ("(".equals(chr)) {
ptr++;
state_No_stack.push(13);
} else if ("ID".equals(chr)) {
ptr++;
state_No_stack.push(14);
}else {
System.out.println("!!! wrong");
return;
}
break;
case 24:
if ("+".equals(chr)) {
ptr++;
state_No_stack.push(20);
} else if (")".equals(chr)) {
ptr++;
state_No_stack.push(29);
} else if ("-".equals(chr)) {
ptr++;
state_No_stack.push(21);
}else {
System.out.println("!!! wrong");
return;
}
break;
case 25:
if ("/".equals(chr)) {
ptr++;
state_No_stack.push(23);
} else if ("*".equals(chr)) {
ptr++;
state_No_stack.push(22);
} else if ("+".equals(chr) || ")".equals(chr) || "-".equals(chr)) {
for (int i = 0; i < 3; i++) {
state_No_stack.pop();
}
System.out.println("expr -> expr + term");
state_No_stack.push(GOTO(state_No_stack.peek(), "expr"));
}else {
System.out.println("!!! wrong");
return;
}
break;
case 26:
if ("/".equals(chr)) {
ptr++;
state_No_stack.push(23);
} else if ("*".equals(chr)) {
ptr++;
state_No_stack.push(22);
} else if ("+".equals(chr) || ")".equals(chr) || "-".equals(chr)) {
for (int i = 0; i < 3; i++) {
state_No_stack.pop();
}
System.out.println("expr -> expr - term");
state_No_stack.push(GOTO(state_No_stack.peek(), "expr"));
}else {
System.out.println("!!! wrong");
return;
}
break;
case 27:
if ("+".equals(chr) || ")".equals(chr) || "/".equals(chr) || "-".equals(chr) || "*".equals(chr)) {
for (int i = 0; i < 3; i++) {
state_No_stack.pop();
}
System.out.println("term -> term * factor");
state_No_stack.push(GOTO(state_No_stack.peek(), "term"));
}else {
System.out.println("!!! wrong");
return;
}
break;
case 28:
if ("+".equals(chr) || ")".equals(chr) || "/".equals(chr) || "-".equals(chr) || "*".equals(chr)) {
for (int i = 0; i < 3; i++) {
state_No_stack.pop();
}
System.out.println("term -> term / factor");
state_No_stack.push(GOTO(state_No_stack.peek(), "term"));
}else {
System.out.println("!!! wrong");
return;
}
break;
case 29:
if ("+".equals(chr) || ")".equals(chr) || "/".equals(chr) || "-".equals(chr) || "*".equals(chr)) {
for (int i = 0; i < 3; i++) {
state_No_stack.pop();
}
System.out.println("factor -> ( expr )");
state_No_stack.push(GOTO(state_No_stack.peek(), "factor"));
}else {
System.out.println("!!! wrong");
return;
}
break;
                default:
                    System.out.println("!!! wrong");
                    return;
            }
        }
    }

    private static int GOTO(int state, String nonterminal) {
        switch (state) {
case 0:
if ("expr".equals(nonterminal)) {
return 1;
}
if ("term".equals(nonterminal)) {
return 2;
}
if ("factor".equals(nonterminal)) {
return 3;
}
return -1;
case 4:
if ("expr".equals(nonterminal)) {
return 10;
}
if ("term".equals(nonterminal)) {
return 11;
}
if ("factor".equals(nonterminal)) {
return 12;
}
return -1;
case 6:
if ("term".equals(nonterminal)) {
return 15;
}
if ("factor".equals(nonterminal)) {
return 3;
}
return -1;
case 7:
if ("term".equals(nonterminal)) {
return 16;
}
if ("factor".equals(nonterminal)) {
return 3;
}
return -1;
case 8:
if ("factor".equals(nonterminal)) {
return 17;
}
return -1;
case 9:
if ("factor".equals(nonterminal)) {
return 18;
}
return -1;
case 13:
if ("expr".equals(nonterminal)) {
return 24;
}
if ("term".equals(nonterminal)) {
return 11;
}
if ("factor".equals(nonterminal)) {
return 12;
}
return -1;
case 20:
if ("term".equals(nonterminal)) {
return 25;
}
if ("factor".equals(nonterminal)) {
return 12;
}
return -1;
case 21:
if ("term".equals(nonterminal)) {
return 26;
}
if ("factor".equals(nonterminal)) {
return 12;
}
return -1;
case 22:
if ("factor".equals(nonterminal)) {
return 27;
}
return -1;
case 23:
if ("factor".equals(nonterminal)) {
return 28;
}
return -1;
            default:
                return -1;
        }
    }

    public static void main(String[] args) throws IOException {
        String text = new String(Files.readAllBytes(Paths.get("./src/to_be_parsed1.txt")), StandardCharsets.UTF_8);
        List<String> input = new ArrayList<>(Arrays.asList(text.split("[ \t\n]")));
        input.add("$");
        parse(input);
    }
}
