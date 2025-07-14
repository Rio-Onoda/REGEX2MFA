import org.antlr.v4.runtime.tree.ParseTreeVisitor;

abstract class RegexNode {}

public class AST extends PCREBaseVisitor<RegexNode> {
    private int captureCounter = 1;//キャプチャ参照用に番号を振る

    @Override
    public RegexNode visitPcre(PCREParser.PcreContext ctx) {
        //System.out.println("visitPcre: " + ctx.getText());
        return visit(ctx.alternation());
    }

    
    @Override
    public RegexNode visitAlternation(PCREParser.AlternationContext ctx) {
        //System.out.println("visitAlternation: " + ctx.getText());
        RegexNode node = visit(ctx.expr(0));
        for (int i = 1; i < ctx.expr().size(); i++) {
            node = new UnionNode(node, visit(ctx.expr(i)));
        }
        return node;
    }

    @Override
    public RegexNode visitExpr(PCREParser.ExprContext ctx) {
        //System.out.println("visitExpr: " + ctx.getText());
        
        if (ctx.element().size() == 1) {
            return visit(ctx.element(0));
        }

        RegexNode node = visit(ctx.element(0));//複数
        for (int i = 1; i < ctx.element().size(); i++) {
            node = new ConcatNode(node, visit(ctx.element(i)));
        }
        return node;
    }

    @Override
    public RegexNode visitElement(PCREParser.ElementContext ctx) {
        RegexNode base = visit(ctx.atom());
        
        //System.out.println("visitElement: " + ctx.getText()); 
        if (ctx.quantifier() != null) {
            String q = ctx.quantifier().getText();
            switch (q) {
                case "*": return new StarNode(base);//0回以上
                case "+": return new ConcatNode(base, new StarNode(base));//1回以上
                default: throw new IllegalArgumentException("unknown quantifier: " + q);
            }
        }
        
        return base;
    }

    @Override
    public RegexNode visitAtom(PCREParser.AtomContext ctx) {
        //System.out.println("visitAtom: " + ctx.getText());
        if (ctx.letter() != null) {
            return visitLetter(ctx.letter());
        }
        if (ctx.capture() != null) {
            return visitCapture(ctx.capture());
        }
        if (ctx.backreference() != null) {
            return visitBackreference(ctx.backreference());
        }
        throw new IllegalArgumentException("未対応のAtom: " + ctx.getText());
    }
    
    @Override
    public RegexNode visitLetter(PCREParser.LetterContext ctx) {
    return new CharNode(ctx.getText().charAt(0));
    }

    @Override
    public RegexNode visitCapture(PCREParser.CaptureContext ctx) {
    //System.out.println("visitCapture: " + ctx.getText());
    //System.out.println("alternation is " + ctx.alternation());
   
    if (ctx.getText().startsWith("(?:")) {
       
        RegexNode child = visit(ctx.alternation());
        return new NonCaptureNode(child);
        
        
    }else if (ctx.alternation() != null) {
        RegexNode child = visit(ctx.alternation());
        return new CaptureNode(child, captureCounter++);
    }

    throw new IllegalArgumentException("CaptureContextにalternationがありません: " + ctx.getText());
}

    @Override
    public RegexNode visitBackreference(PCREParser.BackreferenceContext ctx) {
        System.out.println("visitBackreference: " + ctx.getText());
        int refId = Integer.parseInt(ctx.getText().substring(2));  // 例: \g1
        return new BackrefNode(refId);
    }

    
}
