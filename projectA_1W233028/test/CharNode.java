public class CharNode extends RegexNode {
    public final String symbol;
    public CharNode(String symbol) { 
        this.symbol = symbol; 
    }  

    @Override
    public String toString() {
        return String.valueOf(symbol);
    }
}
