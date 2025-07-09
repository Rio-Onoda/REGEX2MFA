public class UnionNode extends RegexNode {
    public final RegexNode left, right;
    public UnionNode(RegexNode left, RegexNode right) {
        this.left = left;
        this.right = right;
    }
} 
