import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;

class CapturePointer{//キャプチャ位置を保存
    int open;//open 字目から
    int close;//close-1 字目までをキャプチャ

    public CapturePointer(int open,int close){
        this.open = open;
        this.close = close;
    }
}

class Configuration {

    State state;
    String input;//最初の入力文字列 メモリ参照に利用
    String remain;//残りの文字列
    ArrayList<Boolean> memorystates;//メモリの状態　Open or Closed
    ArrayList<CapturePointer> memorycapptr;//メモリの内容　
    int visittransition;//訪れた遷移のカウント

    public void config(State q,String w){//計算状況のコンストラクタ
        this.state = q;
        this.input = w;
        this.remain = w;
        this.memorystates = new ArrayList<>();
        this.memorycapptr = new ArrayList<>();
        this.visittransition = 0;//訪れた遷移のカウント
        
    }

    void copyConfig(Configuration C){//計算状況のコピー
        this.state = C.state;
        this.input = C.input;
        this.remain = C.remain;
        this.memorycapptr = new ArrayList<>(C.memorycapptr);
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
            && memorycapptr.equals(other.memorycapptr));
    }

    @Override
    public int hashCode() {
        return Objects.hash(state.id, remain, memorystates, memorycapptr);
    }
  
}

public class SimMFA {
    Configuration C = new Configuration();
    HashSet<Configuration> configs = new HashSet<>();//訪れた計算状況を記録
    ArrayList<Transition> path = new ArrayList<>();//受理までの経路
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

        for(int i=0;i<numcapture;i++){//メモリ数分確保 idは1から始まる
            C.memorystates.add(false);//Closed
            C.memorycapptr.add(new CapturePointer(-1,-1));//初めはopen,closeともに-1,-1としておく
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
                    if (t.q.id == s && t.symbol.equals("ε") ) {
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
            else{//空白文字を読む遷移が無いか確認する
                for (Transition t : M.transitions) {
                    if(t.symbol.equals(" ")){
                        return true;
                    }
                }
                return false;//受理状態に到達していない
            } 
        }else if(C.remain.length() > 0){
            int skip = count; // 既に調べた遷移はスキップする
            
            count = 0; // 遷移のカウントをリセット
            for (Transition t : M.transitions) {

                if (skip >= count && skip > 0) {//既に調べた遷移はスキップする
                    count++;
                    continue; // スキップする
                }

                if(ecl.contains(t.q.id)) {//ε-closureに含まれる状態で
                     if((t.symbol.charAt(0))==(C.remain.charAt(0))) {//シンボルが残りの文字列の先頭と同じならば
                    
                        String nextRemain = C.remain.substring(1);//次の残りの文字列
                        if(!nextRemain.contains(t.symbol+"")){//以降の文字列にそのシンボルが出現しないなら
                            //System.out.println(t.symbol + " is no longer in the remaining string");
                            C.visittransition = count;//遷移リストにおいて訪れた遷移のカウント
                        }else{//出現するなら
                            C.visittransition = 0;//次回も遷移リストを先頭から調べる
                        }
                        return true;

                    }else if( t.symbol.equals("\\")){// memory transition
                        CapturePointer contentptr = C.memorycapptr.get(t.memoryinstruction.captureId-1);//メモリ内容を取得
                        int open = contentptr.open;
                        int close = contentptr.close;

                        if(close == -1) return true;//closeが未定義だが\\までのε遷移でmemoryがcloseするとする
                           
                        int len = close-open;
                        String prefix;
                        if(C.remain.length() >= len ) prefix = C.remain.substring(0,len);//現在の入力の残りからメモリ長だけ切り出す
                        else    prefix = C.remain;
                
                        String content = C.input.substring(open,close);

                        if(prefix.equals(content))  return true;//メモリ内容と現在の入力の残りを比較
                        else    return false;//メモリ内容と現在の入力の残りに食い違いがある
                        
                    }else if(t.symbol.length() > 1){
                        return true;//処理が複雑なので一旦true
                    }
                    count++;
                }
            }
        }
        return false;
    }

    public void printcapptr(Configuration C){//各メモリのキャプチャ位置を表示
        for (int i = 0;i < C.memorycapptr.size();i++){
            System.out.println("memory "+ (i+1) + ":" + C.memorycapptr.get(i).open +"~"+ C.memorycapptr.get(i).close);
        }
    }

    public void simulation(MFA M,Configuration C,int readpos,int level) {//シミュレーション

        //System.out.print("  level " + level);//再帰の階層
        //System.out.print("    state " + C.state.id);
        //System.out.print("  readpos " + readpos);//何文字目まで読んだか
        //System.out.print("    remain " + C.remain);
        //System.out.println("    memorystates " + C.memorystates);
        HashSet<Integer> ecl = epsilonclosure(M, C);
        //System.out.println("    ε-closure " + ecl);
        //printcapptr(C);
        
        if (!readable) {
            if (isreadable(M, C, ecl, M.end.id,C.visittransition)) {//読み取り可能か検査
                readable = true;
            } else {
                //if (level > 0) System.out.println("not readable, backtrack to level " + (level-1));
                //else System.out.println("not readable, end simulation");
                if (level == 0) { // level 0 で失敗したら探索失敗として終了
                    System.err.println("\n<<  "+ C.remain +"  is not accepted by the MFA >>>");
                }
                return;
            }

        } 
        
        if (configs.contains(C)) {//訪れた計算状況ならば
            //if (level > 0) System.out.println("already visited, backtrack"+ " to level " + (level-1));
            //else System.out.println("already visited, end simulation");
            return; // そこで打ち切り
        }
    
        configs.add(C);//訪れた計算状況を記録

        if(C.remain.isEmpty() && (C.state.id == M.end.id)){ //残りの文字が空かつ終状態ならば受理
            System.out.println("\n<<<  "+ C.input +"  is accepted by following path>>");
            for(Transition t : path) System.out.println(t);//経路を表示して終了
            System.exit(0);
        }

        if(!C.remain.isEmpty()){
            for(Transition t : M.transitions){//遷移を辿る
                if(t.q.id == C.state.id ){//sigma-transition
                    if((t.symbol.charAt(0)==C.remain.charAt(0)) && t.symbol.length() == 1){
                        path.add(t);//経路を記録
                        readable = false;//読み取り可能とする

                        Configuration Du = new Configuration();
                        Du.copyConfig(C);//コピー
                        Du.state = t.p;//遷移先の状態にセット
                        
                        //1文字読む(sigma-transition)
                        Du.remain = Du.remain.substring(1);//先頭1文字切り出し    
                       
                        simulation(M,Du,readpos+1,level+1);

                    }else if(t.symbol.length() > 1){//特殊ラベル
                        String spsym = t.symbol;

                        if(spsym.contains("..")){//wildcard
                            path.add(t);//経路を記録
                            readable = false;//読み取り可能とする
                            
                            Configuration Du = new Configuration();
                            Du.copyConfig(C);
                            Du.state = t.p;
                            Du.remain = Du.remain.substring(1);//先頭1文字切り出し    
                            
                            simulation(M,Du,readpos+1,level+1);

                        }else if(spsym.contains("-") && spsym.charAt(0)!='^'){//範囲指定の記号
                            String [] parts = spsym.split("-");
                            char from = parts[0].charAt(0);
                            char to = parts[1].charAt(0);
                        
                            for (char i = from; i<= to;i++){
                                if(i == C.remain.charAt(0)){//シンボルが残りの文字列の先頭と同じならば
                                    path.add(t);//経路を記録
                                    readable = false;//読み取り可能とする

                                    Configuration Du = new Configuration();
                                    Du.copyConfig(C);
                                    Du.state = t.p;
                                    Du.remain = Du.remain.substring(1);//先頭1文字切り出し    
                                    
                                    simulation(M,Du,readpos+1,level+1);
                                }
                            }
                        }else if(spsym.charAt(0)=='^' && !spsym.contains("-")){//否定・範囲指定なし
                            String notsym = spsym.substring(1);
                            if(!notsym.contains(C.remain.substring(0,1))){
                                path.add(t);//経路を記録
                                readable = false;//読み取り可能とする
                               
                                Configuration Du = new Configuration();
                                Du.copyConfig(C);
                                Du.state = t.p; 
                                Du.remain = Du.remain.substring(1);//先頭1文字切り出し
                        
                                simulation(M,Du,readpos+1,level+1);
                            }

                        }else if(spsym.charAt(0)=='^' && spsym.contains("-")){//否定・範囲指定あり
                            int index = 0;
                            ArrayList<String> range = new ArrayList<>();
                            boolean passable = true;//この遷移が可能かどうか

                            for(String part : spsym.split("")) {//範囲ごとに分割
                                 if(index%3 == 0 && index > 0){
                                   range.add(spsym.substring(index-2, index+1));//A-Z,a-z,0-9のように分割
                               }
                               index++;
                            }

                            char [] from = new char[range.size()];//各範囲の起点
                            char [] to = new char[range.size()];//各範囲の終点

                            for(int i=0 ; i<range.size() ;i++){//遷移できるかチェックする
                                from[i] = range.get(i).charAt(0);
                                to[i] = range.get(i).charAt(2);

                                for (char j = from[i]; j<= to[i];j++){
                                    if(j == C.remain.charAt(0)) passable = false;//シンボルが残りの文字列の先頭と同じならば読み取り不可とする
                                }
                            }

                            if(passable == true){//遷移可能ならば
                                path.add(t);//経路を記録
                                readable = false;//読み取り可能とする

                                Configuration Du = new Configuration();
                                Du.copyConfig(C);
                                Du.state = t.p; 
                                Du.remain = Du.remain.substring(1);//先頭1文字切り出し
                                
                                simulation(M,Du,readpos+1,level+1);
                            }else{
                                //System.out.println("Negation transition is not possible");
                            }
                        }
                    }

                }
            }
        }
        
            //読む記号がない場合
        for(Transition t : M.transitions){
            if(t.q.id == C.state.id && (t.symbol.equals("ε") || t.symbol.equals(" ") || t.symbol.equals("\\"))){

                Configuration Du = new Configuration();//計算状況のコピー
                Du.copyConfig(C);
                Du.state = t.p;//次の状態にセット
                path.add(t);//経路を記録
       
                switch(t.memoryinstruction.instructionId){
                    case 0://ε遷移　sigma-transition
                        simulation(M,Du,readpos,level+1);
                        break;
                    case 1://ε遷移　o-transition
                        Du.memorystates.set(t.memoryinstruction.captureId-1, true);//指定の番号のメモリをopen
                        Du.memorycapptr.get(t.memoryinstruction.captureId-1).open = readpos;
                        simulation(M,Du,readpos,level+1); 
                        break;

                    case -1://ε遷移　c-transition
                        Du.memorystates.set(t.memoryinstruction.captureId-1, false);//指定の番号のメモリをclose
                        Du.memorycapptr.get(t.memoryinstruction.captureId-1).close = readpos;
                        simulation(M,Du,readpos,level+1);
                        break;

                    case 2://ε遷移　m-transition
                        CapturePointer contentptr = Du.memorycapptr.get(t.memoryinstruction.captureId-1);//メモリ内容を取得
                       
                        int open = contentptr.open;
                        int close = contentptr.close;

                        if(open == -1 || close == -1){//未定義のためindex errorになるので戻る
                            //System.out.println("index error. Backtrack to level " + (level-1));
                            return;
                        }
                        int len = close - open;

                        String prefix;
                        if(Du.remain.length() >= len )  prefix = Du.remain.substring(0,len);//現在の入力の残りの先頭からメモリ長だけ切り出す
                        else   prefix = Du.remain;
                        

                        String content = Du.input.substring(open,close);//キャプチャ位置と入力文字列からメモリ内容を復元
                 
                        if(prefix.equals(content)){//メモリ内容と現在の入力の残りを比較
                            //Du.memorycapptr.set(t.memoryinstruction.captureId-1,"");//メモリ内容はリセットする

                            Du.remain = Du.remain.substring(len);//現在の入力の残りからメモリの内容を消費する
                            simulation(M,Du,readpos + len ,level+1);
                            
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
                        //System.out.println("There is no valid transition. Backtrack to level "+ (level-1));
        }

        if (level == 0) { // level 0 で失敗したら探索失敗として終了
            System.err.println("\n<<<  "+ C.input +"  is not accepted by the MFA >>>");
        }

    }//end of simulation 
}