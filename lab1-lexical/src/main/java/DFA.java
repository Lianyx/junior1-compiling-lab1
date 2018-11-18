import util.Constants;
import util.Util;

import java.util.*;

import static util.Util.*;

public class DFA implements Constants {
    List<Map<Character, Integer>> table = new ArrayList<>();
    List<Set<Integer>> NFA_states = new ArrayList<>(); // 分別通過index相對應…
    // 突然感覺之前全部都應該用set的

    @Override
    public String toString() {
        return "DFA{" +
                "table=" + table +
                ", NFA_states=" + NFA_states +
                '}';
    }

    /**
     * all static methods thereafter
     */
    public static DFA NFA2DFA(NFA nfa) {
        DFA dfa = new DFA();

        // 初始化 I_0
        Set<Integer> I_0 = epsilonClosure(intsToSet(0), nfa);
        dfa.NFA_states.add(I_0);

        for (int ptr = 0; ptr < dfa.NFA_states.size(); ptr++) {
            Set<Integer> NFA_states_for_DFAi = dfa.NFA_states.get(ptr);
            Map<Character, Set<Integer>> next_NFA_states_for_DFAi = new HashMap<>();
            // 這個其实已经在构建所有的nextState了
            // 但是经過下面第二个for的处理之後，set放到NFA_states之後
            // table裡就只需要指向DFA.NFA_states的index

            for (int i : NFA_states_for_DFAi) { // 将NFA的每条个state能到的state都贡献過來
                Map<Character, Set<Integer>> next_NFA_states_for_i = nfa.table.get(i);
                next_NFA_states_for_i.forEach((c, edges) -> {
                    if (EPSILON != c) {
                        next_NFA_states_for_DFAi.merge(c, edges, Util::mergeSet);
                    }
                });
            }

            // 处理
            Map<Character, Integer> oneLine = new HashMap<>();
            next_NFA_states_for_DFAi.forEach((c, edges) -> {
                edges = epsilonClosure(edges, nfa);
                int index = dfa.NFA_states.indexOf(edges);
                if (-1 == index) {
                    dfa.NFA_states.add(edges);
                    oneLine.put(c, dfa.NFA_states.size() - 1);
                } else {
                    oneLine.put(c, index);
                }
            });

            // 添加表项
            dfa.table.add(oneLine);
        }

        return dfa;
    }

    public static DFA minimizeState(DFA dfa, List<List<Integer>> dd) {
        for (List<Integer> d1: dd) {
            List<List<Integer>> newGroups1 = new ArrayList<>();
            // 都放在一起一定会有重复计算。但是。。应该会好寫一点吧
            // 每一波都只是横向的，只借助上一汉的分类來判断是否相等。
            // 每一波的生成结果是另一個List<List<Integer>>

            loop1:
            for (int i : d1) {
                if (newGroups1.isEmpty()) {
                    newGroups1.add(intsToList(i));
                    continue;
                }
                for (List<Integer> newGroup : newGroups1) {
                    int j = newGroup.get(0);
                    if (equivalentState(i, j, dfa, dd)) {
                        newGroup.add(j);
                        continue loop1;
                    }
                }

                newGroups1.add(intsToList(i));
            }
        }

        return new DFA();
    }

    private static boolean equivalentState(int i, int j, DFA dfa, List<List<Integer>> dd) {
        return false;
    }

    /**
     * all private methods thereafter
     */
    private static Set<Integer> epsilonClosure(Set<Integer> t, NFA nfa) {
        ArrayList<Integer> list = new ArrayList<>(t);

        for (int ptr = 0; ptr < list.size(); ptr++) {
            int current_NFA_state = list.get(ptr);
            Map<Character, Set<Integer>> nexts = nfa.table.get(current_NFA_state);
            Set<Integer> epsilonMoves = nexts.getOrDefault(EPSILON, new HashSet<>());

            for (int i : epsilonMoves) {
                if (!list.contains(i)) {
                    list.add(i);
                }
            }
        }
        return new HashSet<>(list);
    }
}
















































