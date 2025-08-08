public class RangeNode extends RegexNode {
    public final String from;
    public final String to;

    public RangeNode(String from, String to) {
        this.from = from;
        this.to = to;
    }

    public String toString() {
        return "RangeNode(" + from + ", " + to + ")";
    }
}