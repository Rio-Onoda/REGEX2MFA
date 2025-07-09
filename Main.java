import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

public class Main {
    public static void main(String[] args) throws Exception {
        String regex = "(a|b)*c\\g1";  // ← 正規表現入力
        //test
        //"(a|b)\\g1";  // 正規表現例
        //"(a*)*";  
        //"(a|b|c)*d";

        PCRELexer lexer = new PCRELexer(CharStreams.fromString(regex));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        PCREParser parser = new PCREParser(tokens);
        ParseTree tree = parser.pcre();
        System.out.println("構文木");
        System.out.println(tree.toStringTree(parser));
        
        AST astBuilder = new AST();
        RegexNode root = astBuilder.visit(tree);//ASTの根ノード
        if(root == null) throw new IllegalArgumentException("AST構築失敗");

        GenMFA builder = new GenMFA();
        MFA result = builder.builders(root);//ASTからMFA
        builder.printtransitions(result);//遷移を表示
    }
} 
