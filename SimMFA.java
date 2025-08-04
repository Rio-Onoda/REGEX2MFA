//TOdo config memorycontentsを文字列ではなくはじめと終わりの位置(int)で記録する
//n^(2k+2)
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;

class Configuration {

    State state;
    String remain;//残りの文字列
    ArrayList<Boolean> memorystates;//メモリの状態　Open or Closed
    ArrayList<String> memorycontents;//メモリの内容　
    int visittransition;//訪れた遷移のカウント

    public void config(State q,String w){//計算状況のコンストラクタ
        this.state = q;
        this.remain = w;
        this.memorystates = new ArrayList<>();
        this.memorycontents = new ArrayList<>();
        this.visittransition = 0;//訪れた遷移のカウント
        
    }

    void copyConfig(Configuration C){//計算状況のコピー
        this.state = C.state;
        this.remain = C.remain;
        this.memorycontents = new ArrayList<>(C.memorycontents);
        this.memorystates = new ArrayList<>(C.memorystates);
        this.visittransition = C.visittransition;

    }


    @Override
    public boolean equals(Object o) {//configurationの比較
        if (!(o instanceof Configuration)) return false;
        Configuration other = (Configuration) o;
        return (state.id == (other.state.id)
            && remain.equals(other.remain)
            && memorystates.equals(other.memorystates)
            && memorycontents.equals(other.memorycontents));
    }

    @Override
    public int hashCode() {
        return Objects.hash(state.id, remain, memorystates, memorycontents);
    }
  
}

public class SimMFA {
    Configuration C = new Configuration();
    HashSet<Configuration> configs = new HashSet<>();//訪れた計算状況を記録
    ArrayList<Transition> path = new ArrayList<>();//受理までの経路
    String input = "";//入力文字列
    boolean inputflag = true;//初期の入力文字列を取得
    boolean readable = false;//読み取り可能かどうか

    int numcapture = 0;//括弧の数＝メモリ数


    public Configuration init(MFA M ,String w) {//初期化
        
        if(w.equals("ε")|| w.equals(" ")){//入力文字列がεならば {
            w = "";//入力文字列がεならば空文字列とする
        }
        C.config(M.start,w);//計算状況を初期化

        for(int i=0;i<M.transitions.size();i++){//括弧(必要なメモリ)の数を調べる
            if(numcapture < M.transitions.get(i).memoryinstruction.captureId) {
                numcapture = M.transitions.get(i).memoryinstruction.captureId;
            }
        }

        for(int i=0;i<numcapture;i++){//メモリ数分確保 idは１から始まるので注意
            C.memorystates.add(false);//Closed
            C.memorycontents.add("");//はじめは空
        }
        
        return C;
    }

    public HashSet<Integer> epsilonclosure(MFA M, Configuration C){
        HashSet<Integer> closure = new HashSet<>();
        closure.add(C.state.id);

        while (true) {
            int size = closure.size();
            for (int s : new ArrayList<>(closure)) { // 新しいリストを作成してイテレート
                for (Transition t : M.transitions) {
                    if (t.q.id == s && t.symbol == 'ε') {
                        closure.add(t.p.id); // ε遷移の先の状態を追加
                    }
                }
            }
            if (closure.size() == size) break; // 変化がなければ終了
        }

        return closure;
    }


    public boolean isreadable(MFA M, Configuration C, HashSet<Integer> ecl,int final_state,int count) {
        if(C.remain.isEmpty()){
            if(ecl.contains(final_state)) return true;//残りの文字が空なら受理状態がε-closureに含まれるか検査する
            else return false;//受理状態に到達していない
        }else if(C.remain.length() > 0){
            int skip = count; // 既に調べた遷移はスキップする
            
            count = 0; // 遷移のカウントをリセット
            for (Transition t : M.transitions) {

                if (skip >= count && skip > 0) {//既に調べた遷移はスキップする
                    count++;
                    continue; // スキップする
                }

                if(ecl.contains(t.q.id)) {//ε-closureに含まれる状態ならば
                    if(t.symbol == C.remain.charAt(0)) {//シンボルが残りの文字列の先頭と同じならば
                        //System.out.println(C.remain.charAt(0)  + "  " + t.symbol);
                        

                        String nextRemain = C.remain.substring(1);//次の残りの文字列
                        if(!nextRemain.contains(t.symbol+"")){
                            //System.out.println(t.symbol + " is no longer in the remaining string");
                            C.visittransition = count;//訪れた遷移のカウント
                        }else{
                            C.visittransition = 0;
                        }
                        return true;

                    }else if( t.symbol == '\\'){// memory transition
                        String content = C.memorycontents.get(t.memoryinstruction.captureId-1);//メモリ内容を取得
                        String prefix;
                        if(C.remain.length() >= content.length()){
                            prefix = C.remain.substring(0,content.length());//現在の入力の残りからメモリ長だけ切り出す
                        }else{
                            prefix = C.remain;
                        }

                        if(prefix.equals(content)){//メモリ内容と現在の入力の残りを比較
                            //System.out.println("m-transition: " + prefix + " == " + content);
                           return true;
                        }else{
                            //System.out.println("m-transition error: " + prefix + " != " + content);
                            return false;//メモリ内容と現在の入力の残りに食い違いがある
                        }
                    }
                    count++;
                }
            }
        }
        return false;
    }

    public void simulation(MFA M,Configuration C,int level) {//シミュレーション

        System.out.print("  level " + level);//再帰の階層
        System.out.print("    state " + C.state.id);
        //System.out.print("    remain " + C.remain);
        System.out.print("    memorycontents " +C.memorycontents);
        //System.out.println("    memorystates " + C.memorystates);
        HashSet<Integer> ecl = epsilonclosure(M, C);
        System.out.println("    ε-closure " + ecl);
        
        if (!readable) {
            if (isreadable(M, C, ecl, M.end.id,C.visittransition)) {//読み取り可能か検査
                readable = true;
               
            } else {
                if (level > 0) System.out.println("not readable, backtrack to level " + (level-1));
                else System.out.println("not readable, end simulation");
                return;
            }

        } 
        
        if (configs.contains(C)) {//訪れた計算状況ならば
            if (level > 0) System.out.println("already visited, backtrack"+ " to level " + (level-1));
            else System.out.println("already visited, end simulation");
            return; // そこで打ち切り
        }
    
        configs.add(C);//訪れた計算状況を記録
        
        if(inputflag == true){//初期の入力文字列を取得
            input = C.remain;
            if(input.length() == 0) input = "ε";
            inputflag = false;//取得済みとする
        }

        if(C.remain.isEmpty() && (C.state.id == M.end.id)){ //残りの文字が空かつ終状態ならば受理
            System.out.println("\n<<  "+ input +"  is accepted by following path>>");
            for(Transition t : path) System.out.println(t);//経路を表示して終了
            System.exit(0);
        }

        if(!C.remain.isEmpty()){
            for(Transition t : M.transitions){//遷移を辿る
                if(t.q.id == C.state.id && t.symbol == C.remain.charAt(0)){//sigma-transition
                    
                    path.add(t);//経路を記録
                    readable = false;//読み取り可能とする

                    char ch = t.symbol;
                    Configuration Du = new Configuration();
                    Du.copyConfig(C);
                    Du.state = t.p;
                        
                    //1文字読む(sigma-transition)
                    Du.remain = Du.remain.substring(1);//先頭1文字切り出し    
                    for(int i=0;i<Du.memorystates.size();i++){//openメモリへの書き込み
                        if(Du.memorystates.get(i) == true){//openメモリならば
                            
                            if(Du.memorycontents.get(i) == null){//ヌルチェック
                                Du.memorycontents.set(i,"");//メモリ内容は空とする
                            }  
                            Du.memorycontents.set(i,Du.memorycontents.get(i)+ch);//指定のメモリに後ろから1字ずつ書き込む
                        
                        }
                   
                    }
                    //System.out.println(t);
                    simulation(M,Du,level+1);
                    
                }
            }

        }
        
        //読む記号がない場合
        for (Transition t : M.transitions){
            if(t.q.id == C.state.id && (t.symbol == 'ε' || t.symbol == '\\')){//ε遷移の場合
                    
                    Configuration Du = new Configuration();//計算状況のコピー
                    Du.copyConfig(C);
                    Du.state = t.p;//次の状態にセット
                    path.add(t);//経路を記録
       
                    switch(t.memoryinstruction.instructionId){
                        case 0://ε遷移　sigma-transition
                            //System.out.println(t);
                            simulation(M,Du,level+1);
                            break;
                        case 1://ε遷移　o-transition
                            Du.memorystates.set(t.memoryinstruction.captureId-1, true);//指定の番号のメモリをopen
                            //System.out.println(t);
                            simulation(M,Du,level+1); 
                            break;

                        case -1://ε遷移　c-transition
                            Du.memorystates.set(t.memoryinstruction.captureId-1, false);//指定の番号のメモリをclose
                            //System.out.println(t);
                            simulation(M,Du,level+1);
                            break;

                        case 2://ε遷移　m-transition
                            String content = Du.memorycontents.get(t.memoryinstruction.captureId-1);//メモリ内容を取得
                            String prefix; 
                            if(Du.remain.length() >= content.length()){
                                prefix = Du.remain.substring(0,content.length());//現在の入力の残りからメモリ長だけ切り出す
                            }else{
                                prefix = Du.remain;
                            }
                            
                            if(prefix.equals(content)){//メモリ内容と現在の入力の残りを比較
                                for(int i=0;i<Du.memorystates.size();i++){//openメモリへの書き込み
                                    if(Du.memorystates.get(i) == true){//openメモリならば
                            
                                        if(Du.memorycontents.get(i) == null){//ヌルチェック
                                            Du.memorycontents.set(i,"");//メモリ内容は空とする
                                        }     
                                        
                                        Du.memorycontents.set(i,Du.memorycontents.get(i)+prefix);//指定のメモリに後ろから切り出したものを書き込む
                        
                                    }
                   
                                }
                                //Du.memorycontents.set(t.memoryinstruction.captureId-1,"");//メモリ内容はリセットする

                                Du.remain = Du.remain.substring(content.length());//現在の入力の残りからメモリの内容を消費する
                                //System.out.println(t);
                                simulation(M,Du,level+1);
                            
                            }else{
                                //System.err.println("m-transition error");//メモリ内容と現在の入力の残りに食い違いがある
                                return;
                            }
                            break;
                    }     
                
    
                }
            }

         if(!path.isEmpty() && level > 0){
                        path.remove(path.size()-1);//経路を削除
                        System.out.println("There is no valid transition. Backtrack to level "+ (level-1));
        }



        if (level == 0) { // level 0 で失敗したら探索失敗として終了
            System.err.println("\n<<  "+ input +"  is not accepted by the MFA >>>");
        }

    }//end of simulation 
    
}
   