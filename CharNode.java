public class CharNode extends RegexNode {
    public final char symbol;
    public CharNode(char symbol) { 
        this.symbol = symbol; 
    }  

    @Override
    public String toString() {
        return String.valueOf(symbol);
    }
}
