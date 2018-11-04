import RE.RENode;
import util.Constants;
import util.Util;

import java.util.*;
import java.util.stream.Collectors;

import static util.Util.*;

public class NFA implements Constants {
    List<Map<Character, Set<Integer>>> table = new ArrayList<>();
    // start永遠是第一個，end永遠是最後一個

    private NFA() {
    }

    @Override
    public String toString() {
        return "NFA{" +
                "table=" + table +
                '}';
    }

    /**
     * all static methods thereafter
     */

    static NFA postRegexToNFA(List<RENode> postRegex) {
        Deque<NFA> stack = new LinkedList<>();
        for (RENode reNode : postRegex) {
            char c = reNode.ch;
            NFA n1, n2;
            if (RENode.Type.OP == reNode.type) {
                switch (c) {
                    case DOT:
                        n2 = stack.pop();
                        n1 = stack.pop();
                        stack.push(append(n1, n2));
                        break;
                    case VERTICAL_LINE:
                        n2 = stack.pop();
                        n1 = stack.pop();
                        stack.push(or(n1, n2));
                        break;
                    case ASTERISK:
                        n1 = stack.pop();
                        stack.push(asterisk(n1));
                        break;
                }
            } else {
                stack.push(primitiveNFA(c));
            }
        }
        return stack.pop();
    }

    public static NFA combine(List<NFA> NFAs, List<Integer> newEndStates) { // 這种種口太丑了
        NFA nfa = new NFA();

        // step1: add start state
        Map<Character, Set<Integer>> first = new HashMap<>();
        nfa.table.add(first);

        // step2: add states respectively, compute newEndstate by the way
        int addend = 1;
        for (NFA nfa_i : NFAs) {
            first.merge(EPSILON, intsToSet(addend), Util::mergeSet);
            nfa.table.addAll(copyAndShiftTable(nfa_i.table, addend));
            addend += nfa_i.table.size();
            newEndStates.add(addend - 1);
        }

        return nfa;
    }


    /**
     * private
     */
    private static NFA primitiveNFA(char c) {
        NFA nfa = new NFA();
        Map<Character, Set<Integer>> first = new HashMap<>();
        first.put(c, intsToSet(1));
        nfa.table.add(first);
        nfa.table.add(new HashMap<>());
        return nfa;
    }

    private static NFA or(NFA n1, NFA n2) {
        NFA nfa = new NFA();

        // Step1: add start state and two edges
        Map<Character, Set<Integer>> first = new HashMap<>();
        first.put(EPSILON, intsToSet(1, 1 + n1.table.size()));
        nfa.table.add(first);

        // Step2: add n1 and n2
        nfa.table.addAll(copyAndShiftTable(n1.table, 1));
        nfa.table.addAll(copyAndShiftTable(n2.table, 1 + n1.table.size()));

        // Step3: add End State and two edges
        nfa.table.add(new HashMap<>());
        Map<Character, Set<Integer>> n1End = nfa.table.get(n1.table.size());
        Map<Character, Set<Integer>> n2End = nfa.table.get(n1.table.size() + n2.table.size());
        n1End.merge(EPSILON, intsToSet(1 + n1.table.size() + n2.table.size()), Util::mergeSet); // 有点不敢用singletonList
        n2End.merge(EPSILON, intsToSet(1 + n1.table.size() + n2.table.size()), Util::mergeSet);

        return nfa;
    }

    private static NFA append(NFA n1, NFA n2) {
        NFA nfa = new NFA();

        // Step1: 但只是对n1做一下deep copy，這樣感覺好一點…
        nfa.table = copyAndShiftTable(n1.table, 0);

        // Step2: 处理中間的连接（在其实我覺得加一条ε邊更方便）
        Map<Character, Set<Integer>> n1End = nfa.table.get(nfa.table.size() - 1);
        Map<Character, Set<Integer>> n2Start = n2.table.get(0);
        n2Start.forEach((c, edges) -> {
            Set<Integer> newEdges = copyAndShiftList(edges, n1.table.size() - 1);
            n1End.merge(c, newEdges, Util::mergeSet);
        });

        // Step3: 加上n2
        nfa.table.addAll(copyAndShiftTable(
                n2.table.subList(1, n2.table.size()),
                n1.table.size() - 1
        ));

        return nfa;
    }

    private static NFA asterisk(NFA n1) {
        NFA nfa = new NFA();

        // Step1: add Start State and two edge
        Map<Character, Set<Integer>> first = new HashMap<>();
        first.put(EPSILON, intsToSet(1, 1 + n1.table.size()));
        nfa.table.add(first);

        // Step2: add n1 and two edge
        nfa.table.addAll(copyAndShiftTable(n1.table, 1));
        Map<Character, Set<Integer>> n1End = nfa.table.get(n1.table.size());
        n1End.merge(EPSILON, intsToSet(1, 1 + n1.table.size()), Util::mergeSet);

        // Step3: add End State
        nfa.table.add(new HashMap<>());

        return nfa;
    }

    /**
     * all utility functions thereafter
     */

    private static Set<Integer> copyAndShiftList(Set<Integer> previous, int addend) {
        return previous.stream().map(i -> i + addend)
                .collect(Collectors.toSet());
    }

    private static List<Map<Character, Set<Integer>>> copyAndShiftTable(
            List<Map<Character, Set<Integer>>> previous, int addend) {
        return previous.stream().map(item ->
                item.entrySet().stream().collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> copyAndShiftList(e.getValue(), addend)
                ))
        ).collect(Collectors.toList());
    }
}










































