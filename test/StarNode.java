public class StarNode extends RegexNode {
    public final RegexNode child;
    public StarNode(RegexNode child) {
        this.child = child;
    }

    public String toString() {
        return "StarNode(" + child +')';
    }
} 
