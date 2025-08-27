class ConcatNode extends RegexNode {
    public final RegexNode left, right;
    public ConcatNode(RegexNode left, RegexNode right) {
        this.left = left;
        this.right = right;
    }
    @Override
    public String toString() {
        return "Concat(" + left + ", " + right + ")";
    }
}
