import java.io.ObjectInputFilter.Config;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.logging.SimpleFormatter;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

public class Main {
    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        
        String regex = args[0];  //  正規表現入力
        String w = args[1];
        //String regex = reader.readLine();  //  正規表現入力
        //String w = reader.readLine();

        System.out.println("正規表現: " + regex);
        System.out.println("入力文字列: "+ w) ;
        


        //reader.readLine();
        //test
        //"((?:a|b)*)\g1" abaaabaa

        PCRELexer lexer = new PCRELexer(CharStreams.fromString(regex));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        PCREParser parser = new PCREParser(tokens);
        ParseTree root = parser.pcre();
        //System.out.println("構文木");
        //System.out.println(root.toStringTree(parser));
        
        AST myast = new AST();
        RegexNode astroot = myast.visit(root);//ASTの根ノード
        if(astroot == null) throw new IllegalArgumentException("AST構築失敗");

        //System.out.println("AST");
        //System.out.println(astroot.toString());

        GenMFA mfa = new GenMFA();
        SimMFA simulator = new SimMFA();
        MFA M = mfa.builders(astroot);//ASTからMFA
        mfa.printtransitions(M);//遷移を表示
        System.out.println("qi:" + M.start.id + " qf:" + M.end.id);
       
        
        //reader.readLine();

        Configuration C = new Configuration();
        C = simulator.init(M,w);//初期化

        simulator.simulation(M,C,0);//シミュレーション

    }
} 
