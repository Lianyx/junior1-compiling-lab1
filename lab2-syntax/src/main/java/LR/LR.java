package LR;

import grammer.Production;
import grammer.Symbol;
import grammer.SymbolType;

import java.util.*;
import java.util.stream.Collectors;

import static grammer.Grammar.*;
import static util.Util.*;

public class LR {
    // 构建好之後，所有state都在這裡，整个程序中不会出現其他state的reference。
    public static List<State> LR_automation = new ArrayList<>();

    public static void constructAutomation() {
        Item item_0 = new Item(all_productions.get(0), arrayToSet(endSymbol));
        List<Item> kernal_of_state_0 = Arrays.asList(item_0);
        State state_0 = new State(kernal_of_state_0, inStateExtension(kernal_of_state_0));
        LR_automation.add(state_0);

        // 这个明明不能换，ide报的不行
        for (int i = 0; i < LR_automation.size(); i++) {
            State state_i = LR_automation.get(i);

            for (Item itm : state_i.items) {
                Symbol edge = itm.nextSymbol();

                if (endSymbol != edge && !state_i.next_states.containsKey(edge)) {
                    List<Item> kernal_of_next_state = state_i.items.stream()
                            .filter(x -> edge == x.nextSymbol())
                            .map(Item::move)
                            .collect(Collectors.toList());
                    State next_state = kernalToState(kernal_of_next_state);
                    state_i.next_states.put(edge, next_state);
                }
            }

        }
    }

    // 應該只是为了toString
    static int getStateNo(State state) {
        return LR_automation.indexOf(state);
    }

    /**
     * all private methods thereafter
     */
    private static State kernalToState(List<Item> kernal_of_stts) {
        for (State state : LR_automation) { // 這裡的item equal需要比较afters了呃
            if (state.isEquivalent(kernal_of_stts)) { // TODO 但是kernal不相等就一定整个都不相等嗎？
                return state;
            }
        }
        State new_state = new State(kernal_of_stts, inStateExtension(kernal_of_stts));
        LR_automation.add(new_state);
        return new_state;
    }

    private static List<Item> inStateExtension(List<Item> items) {
        List<Item> result = new ArrayList<>(items);

        for (int ptr = 0; ptr < result.size(); ptr++) {
            Item itm = result.get(ptr);
            Symbol target_head = itm.nextSymbol();

            if (SymbolType.nonterminal == target_head.type) {
                for (Production production : all_productions) { // 其实可以只做一遍，然後全部存在symbol裡
                    if (target_head.equals(production.head)) {
                        Item potential_itm = new Item(production, computeInStateLookafter(itm));
                        if (result.stream().noneMatch(i -> i.production == potential_itm.production
                                && i.dot_pos == potential_itm.dot_pos)) {
                            result.add(potential_itm);
                        } else { // 有可能只是往裡面加一些東西
                            Item old_itm = result.get(result.indexOf(potential_itm));
                            old_itm.afters.addAll(potential_itm.afters);
                        }
                    }
                }
            }
        }

        return result;
    }

    private static Set<Symbol> computeInStateLookafter(Item old_item) {
        Set<Symbol> result = new HashSet<>();

        List<Symbol> to_be_passed_to_first = old_item.nextLookAfter();
        for (Symbol after : old_item.afters) {
            to_be_passed_to_first.add(after);

            result.addAll(first(to_be_passed_to_first));

            to_be_passed_to_first.remove(to_be_passed_to_first.size()-1);
        }

        return result;
    }


}
