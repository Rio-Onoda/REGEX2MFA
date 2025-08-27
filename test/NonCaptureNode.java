public class NonCaptureNode extends RegexNode {
    public final RegexNode child;
    public NonCaptureNode(RegexNode child) {
        this.child = child;
    }

    public String toString() {
        return child.toString();
    }
} 

