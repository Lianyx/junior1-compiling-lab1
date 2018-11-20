package grammer;

import java.util.List;
import java.util.stream.Collectors;

public class Production {
    public Symbol head;
    public List<Symbol> body;

    // 包内可访问，因为只让生成一开始的那几个production，之後只能通過Grammar拿reference
    Production(Symbol head, List<Symbol> body) {
        this.head = head;
        this.body = body;
    }

    @Override
    public String toString() {
        return head + " -> " + String.join(
                " ",
                body.stream().map(Object::toString).collect(Collectors.toList())
        );
    }

    // equal就是reference的equal，不要override
}
