package RE;

public class RENode {
    public char ch;
    public Type type;

    RENode(char ch, Type type) {
        this.ch = ch;
        this.type = type;
    }

    @Override
    public String toString() {
        return "" + ch;
    }

    public enum Type {
        CH, OP
    }
}
