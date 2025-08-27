public class NegNode extends RegexNode {
    public final RegexNode child;

    public NegNode(RegexNode child) {
        this.child = child;
    }

    public String toString() {
        return "NegNode(" + child + ")";
    }
}