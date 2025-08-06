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
            //System.out.println("visitAlternation: " + ctx.expr(i).getText());
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

                default: 
                    if ((q.contains("{"))&&(q.contains("}"))){
                        int len = q.length();
                        boolean nolimit = false;
                        if(len < 3) throw new IllegalArgumentException("unknown quantifier: " + q);

                        if(q.contains(",")){
                            String r = q.substring(1,len-1);
                            
                            if((q.substring(len-2,len-1)).equals(",")){
                                nolimit = true;//下限のみに移行
                            }else{//上限あり

                                String [] range = r.split(",");
                                int s = Integer.parseInt(range[0]);
                                int t = Integer.parseInt(range[1]);
                                
                                RegexNode nodeelem = base;
                                for (int i = 1;i < s; i++){
                                    nodeelem = new ConcatNode(nodeelem,base);
                                }

                                RegexNode nodelst = nodeelem;
                                for (int i = s;i < t; i++){
                                    nodeelem = new ConcatNode(nodeelem,base);
                                    nodelst = new UnionNode(nodelst,nodeelem);
                                }
                            
                                return nodelst;
                            }
                        }
                        
                        if((!q.contains(",")) || nolimit){//下限のみ
                            String r;

                            if (nolimit) r = q.substring(1,len-2);
                            else  r = q.substring(1,len-1);

                            int s = Integer.parseInt(r);

                            if(s<1){
                                return new StarNode(base);//0回以上
                            }else{ 
                                RegexNode node = base;
                                for (int i = 1;i < s; i++){
                                    node = new ConcatNode(node,base);
                                }

                                node = new ConcatNode(node,new StarNode(base));

                                return node;
                            }

                           
                        
                        }
                    }else{
                        throw new IllegalArgumentException("unknown quantifier: " + q);
                    }
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
            //System.out.println("visitBackreference: " + ctx.backreference().getText());
            return visitBackreference(ctx.backreference());
        }

        if(ctx.getText().equals("ε")) {
            return new CharNode('ε'); // εを表すノード
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
   
    if (ctx.getText().startsWith("(?:")) {
       
        RegexNode child = visit(ctx.alternation());
        return new NonCaptureNode(child);
        
        
    }else if (ctx.alternation() != null) {
        int currCaptureNumber = captureCounter++;//キャプチャ参照用の番号

        RegexNode child = visit(ctx.alternation());
        return new CaptureNode(child, currCaptureNumber);
    }

    throw new IllegalArgumentException("CaptureContextにalternationがありません: " + ctx.getText());
}

    @Override
    public RegexNode visitBackreference(PCREParser.BackreferenceContext ctx) {
        int refId = Integer.parseInt(ctx.getText().substring(2));  // 例: \g1
        return new BackrefNode(refId);
    }

    
}
