package LR;

import grammer.Production;
import grammer.Symbol;

import java.util.*;
import java.util.stream.Collectors;

import static grammer.Grammar.all_productions;

public class State {
    private List<Item> kernal;
    List<Item> items;
    Map<Symbol, State> next_states = new HashMap<>();

    State(List<Item> kernal, List<Item> items) {
        this.kernal = kernal;
        this.items = items;
    }

    public boolean isEquivalent(List<Item> kernal) {
        return this.kernal.equals(kernal);
    }


    @Override
    public String toString() {
        return "State{" +
                "items=" + items +
                ", next_states=" +
                next_states.entrySet().stream()
                        .map(e -> new AbstractMap.SimpleEntry<>(e.getKey(), LR.getStateNo(e.getValue())))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
                + '}';
    }

    /**
     * all private methods thereafter
     */
}
