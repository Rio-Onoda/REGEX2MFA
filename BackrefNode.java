public class BackrefNode extends RegexNode {
    public final int captureid;
    public BackrefNode(int captureid) {
        this.captureid = captureid;
    }
}