package LR;

import grammer.Grammar;
import grammer.Production;
import grammer.Symbol;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Item {
    public Production production;
    public Set<Symbol> afters;
    int dot_pos;


    Item(Production production, Set<Symbol> afters) {
        this.production = production;
        this.dot_pos = 0;
        this.afters = afters;
    }

    Symbol nextSymbol() {
        if (isEnd()) {
            return Grammar.endSymbol;
        }
        return production.body.get(dot_pos);
    }

    Item move() {
        Item result = new Item(this.production, this.afters);
        result.dot_pos = this.dot_pos + 1;

        return result;
    }

    // 好难取名字
    List<Symbol> nextLookAfter() {
        return new ArrayList<>(production.body.subList(dot_pos + 1, production.body.size()));
    }


    public boolean isEnd() {
        return dot_pos == production.body.size();
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(production.head + " ->");
        for (int i = 0; i < production.body.size(); i++) {
            sb.append(" ");
            if (dot_pos == i) {
                sb.append("·");
            }
            sb.append(production.body.get(i));
        }

        if (isEnd()) {
            sb.append("·");
        }

        if (!afters.isEmpty()) {
            sb.append(", ");
            sb.append(String.join(
                    "|",
                    afters.stream().map(Object::toString).collect(Collectors.toList()))
            );
        }
        return sb.toString();
    }

    // 三个都要比
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Item item = (Item) o;

        if (dot_pos != item.dot_pos) return false;
        if (production != null ? !production.equals(item.production) : item.production != null) return false;
        return afters != null ? afters.equals(item.afters) : item.afters == null;
    }

    @Override
    public int hashCode() {
        int result = production != null ? production.hashCode() : 0;
        result = 31 * result + dot_pos;
        result = 31 * result + (afters != null ? afters.hashCode() : 0);
        return result;
    }
}
