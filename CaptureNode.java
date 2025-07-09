public class CaptureNode extends RegexNode {
    public final RegexNode child;
    public final int captureid;
    public CaptureNode(RegexNode child, int captureid) {
        this.child = child;
        this.captureid = captureid;
    }
} 
