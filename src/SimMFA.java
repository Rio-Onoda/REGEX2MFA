import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;

class Configuration {

    State state;
    String remain;//残りの文字列
    ArrayList<Boolean> memorystates;//メモリの状態　Open or Closed
    ArrayList<String> memorycontents;//メモリの内容　

    public void config(State q,String w){//計算状況のコンストラクタ
        this.state = q;
        this.remain = w;
        this.memorystates = new ArrayList<>();
        this.memorycontents = new ArrayList<>();
       
        
    }

    void copyConfig(Configuration C){//計算状況のコピー
        this.state = C.state;
        this.remain = C.remain;
        this.memorycontents = new ArrayList<>(C.memorycontents);
        this.memorystates = new ArrayList<>(C.memorystates);

    }

    int searchOpenMemory(){//openメモリを探す
        for(int i=0;i<this.memorystates.size();i++){
            if(this.memorystates.get(i) == true){
                return i;
            }
        }
        return -1;//openメモリなし
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
    HashSet<Configuration> configs = new HashSet<>();
    ArrayList<Transition> path = new ArrayList<>();//受理までの経路
    String input = "";//入力文字列
    boolean inputflag = true;//初期の入力文字列を取得

    int numcapture = 0;//括弧の数＝メモリ数


    public Configuration init(MFA M ,String w) {//初期化
        
        C.config(M.start,w);//計算状況を初期化
        if(C.remain=="") C.remain = "ε";

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



    public void simulation(MFA M,Configuration C,int level) {//シミュレーション

        System.out.print("level " + level);//再帰の階層
        System.out.print("    state " + C.state.id);
        System.out.print("    remain " + C.remain);
        System.out.print("    memorycontents " +C.memorycontents);
        System.out.println("    memorystates " + C.memorystates);

        if (configs.contains(C)) {
            System.out.println("loop");
            return; // 無限ループ防止
        }
        configs.add(C);
        
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

                    char ch = t.symbol;
                    Configuration Du = new Configuration();
                    Du.copyConfig(C);
                    Du.state = t.p;
                        
                    int index = C.searchOpenMemory(); //openメモリの番号(-1されている)
                    
                    if(index < 0){//メモリに書き込まれない
                        Du.remain = Du.remain.substring(1);//先頭1文字切り出し
                        System.out.println(t);
                        simulation(M,Du,level+1);

                        if(!path.isEmpty()){
                        path.remove(path.size()-1);//経路を削除
                        System.out.println("Backtrack bottom");
                    }
                    }else{
                        if(Du.memorycontents.get(index) == null){//ヌルチェック
                            Du.memorycontents.set(index,"");//メモリ内容は空とする
                        }  
                        Du.memorycontents.set(index,Du.memorycontents.get(index)+ch);//指定のメモリに後ろから1字ずつ書き込む
                        Du.remain = Du.remain.substring(1);//先頭1文字切り出し
                        System.out.println(t);
                        simulation(M,Du,level+1);

                        if(!path.isEmpty()){
                        path.remove(path.size()-1);//経路を削除
                        System.out.println("Backtrack bottom");
                    }

                    }                       
                }
            }

        }
                //マッチなし
        for (Transition t : M.transitions){
            if(t.q.id == C.state.id && (t.symbol == 'ε' || t.symbol == '\\')){//ε遷移の場合
                    
                    Configuration Du = new Configuration();//計算状況のコピー
                    Du.copyConfig(C);
                    Du.state = t.p;//次の状態にセット
                    path.add(t);//経路を記録
       
                    switch(t.memoryinstruction.instructionId){
                        case 0://ε遷移　sigma-transition
                            System.out.println(t);
                            simulation(M,Du,level+1);
                            break;
                        case 1://ε遷移　o-transition
                            Du.memorystates.set(t.memoryinstruction.captureId-1, true);//指定の番号のメモリをopen
                            System.out.println(t);
                            simulation(M,Du,level+1); 
                            break;

                        case -1://ε遷移　c-transition
                            Du.memorystates.set(t.memoryinstruction.captureId-1, false);//指定の番号のメモリをclose
                            System.out.println(t);
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
                            
                                Du.remain = Du.remain.substring(content.length());//現在の入力の残りからメモリの内容を消費する
                                System.out.println(t);
                                simulation(M,Du,level+1);
                            }else{
                                //System.err.println("m-transition error");//メモリ内容と現在の入力の残りに食い違いがある
                                return;
                            }
                            break;
                    }     
                }
            }

         if(!path.isEmpty()){
                        path.remove(path.size()-1);//経路を削除
                        System.out.println("Backtrack to level "+ (level-1));
        }



        if (level == 0) { // level 0 で失敗したら探索失敗
            System.err.println("\n<<  "+ input +"  is not accepted by the MFA >>>");
        }

    }//end of simulation 
    
}
