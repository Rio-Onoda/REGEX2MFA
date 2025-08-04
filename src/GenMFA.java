import java.util.List;
import java.util.ArrayList;

class State {
    int id;

    public State(int id) {
        this.id = id;
    }
}

class Transition {
    State q;
    State p;
    char symbol; // q --symbol--> p
    Memoryinstruction memoryinstruction;

    public Transition(State q, char symbol, State p, Memoryinstruction meminst) {// constructor
        this.q = q;
        this.symbol = symbol;
        this.p = p;
        this.memoryinstruction = meminst;
    }

    @Override
    public String toString() {
        String memInstr;
        switch (memoryinstruction.instructionId) {
            case 1: memInstr = "OPEN"; break;
            case -1: memInstr = "CLOSE"; break;
            case 0: memInstr = "STAY"; break;
            case 2: memInstr = "VAR"; break;
            default: memInstr = "UNKNOWN"; break;
    }
    return String.format("δ(%d, %s) = %d   [mem: (%d, %s)]",
        q.id, symbol == 'ε' ? "ε" : symbol, p.id,
        memoryinstruction.captureId, memInstr
    );
}   

}


class Memoryinstruction {
    int captureId; // キャプチャID
    int instructionId; // 命令ID
    public Memoryinstruction(int captindex,int instructid) {
        this.captureId = captindex; //括弧のインデックス
        this.instructionId = instructid;//0:<> 1:o -1:c
    }
}

class MFA {
    State start;
    State end;
    List<Transition> transitions;
        
    public MFA() {
        this.transitions = new ArrayList<>();
    }
}

//ASTをMFAにするクラス

public class GenMFA {
    private final int VARIABLE = 2;
    private final int STAY = 0;
    private final int OPEN = 1;
    private final int CLOSE = -1;
    private int stateId = 0;

    private State newState() {
        return new State(stateId++);
    }

    public MFA builders(RegexNode node) {
        if (node instanceof CharNode) {
            return UnitMFA((CharNode) node);
        } else if (node instanceof ConcatNode) {
            return ConcatMFA((ConcatNode) node);
        } else if (node instanceof UnionNode) {
            return UnionMFA((UnionNode) node);
        } else if (node instanceof StarNode) {
            return StarMFA((StarNode) node);
        } else if (node instanceof CaptureNode) {
            return CaptureMFA((CaptureNode) node);     
        } else if(node instanceof NonCaptureNode) {
            return NonCaptureMFA((NonCaptureNode) node);
        }
        else if (node instanceof BackrefNode) {
            return BackrefMFA((BackrefNode) node);     
        }
        throw new IllegalArgumentException("Unknown node type");
    }

    private MFA UnitMFA(CharNode node) {
        State s1 = newState();
        State s2 = newState();
        Transition t = new Transition(s1, node.symbol, s2, 
        new Memoryinstruction(0, STAY));

        MFA submfa = new MFA();
        submfa.start = s1;
        submfa.end = s2;
        submfa.transitions = List.of(t);
        return submfa;
    }

    private MFA ConcatMFA(ConcatNode node) {
        MFA m1 = builders(node.left);
        MFA m2 = builders(node.right);

        Transition t = new Transition(m1.end, 'ε', m2.start, new Memoryinstruction(0, STAY)); // EPSILON Transition

        List<Transition> transitions = new ArrayList<>();
        transitions.addAll(m1.transitions);
        transitions.addAll(m2.transitions);
        transitions.add(t);

        MFA submfa = new MFA();
        submfa.start = m1.start;
        submfa.end = m2.end;
        submfa.transitions = transitions;
        return submfa;
    }


    private MFA UnionMFA(UnionNode node) {
        MFA m1 = builders(node.left);
        MFA m2 = builders(node.right);

        State start = newState();
        State end = newState();

        List<Transition> transitions = new ArrayList<>(m1.transitions);
        transitions.addAll(m2.transitions);

        transitions.add(new Transition(start, 'ε', m1.start, new Memoryinstruction(0, STAY))); // ε Transition to m1
        transitions.add(new Transition(start, 'ε', m2.start, new Memoryinstruction(0, STAY))); // ε Transition to m2
        transitions.add(new Transition(m1.end, 'ε', end,new Memoryinstruction(0, STAY))); // ε Transition from m1 to end
        transitions.add(new Transition(m2.end, 'ε', end, new Memoryinstruction(0, STAY))); // ε Transition from m2 to end

        MFA submfa = new MFA();
        submfa.start = start;
        submfa.end = end;
        submfa.transitions = transitions;
        return submfa;
    }

    private MFA StarMFA(StarNode node) {
        MFA mfa = builders(node.child);
        State start = newState();
        State end = newState();

        List<Transition> transitions = new ArrayList<>(mfa.transitions);

        transitions.add(new Transition(start, 'ε', mfa.start, new Memoryinstruction(0, STAY))); // ε Transition to nfa start
        transitions.add(new Transition(mfa.end, 'ε', end, new Memoryinstruction(0, STAY))); // ε Transition from nfa end to new end
        transitions.add(new Transition(mfa.end, 'ε', mfa.start, new Memoryinstruction(0, STAY))); // ε Transition from nfa end to nfa start (for repetition)
        transitions.add(new Transition(start, 'ε', end, new Memoryinstruction(0, STAY))); // ε Transition from start to end (for empty string)

        MFA submfa = new MFA();
        submfa.start = start;
        submfa.end = end;
        submfa.transitions = transitions;
        return submfa;
    }

   
    private MFA CaptureMFA(CaptureNode node) {
        MFA submfa = builders(node.child);
        State start = newState();
        State end = newState();
    
        // o-transition（メモリ開始）
        Transition t1 = new Transition(start, 'ε', submfa.start,
        new Memoryinstruction(node.captureid, OPEN)); 

        // c-transition（メモリ終了）
        Transition t2 = new Transition(submfa.end, 'ε', end,
            new Memoryinstruction(node.captureid, CLOSE)); 

        MFA mfa = new MFA();
        mfa.start = start;
        mfa.end = end;

        mfa.transitions.add(t1);
        mfa.transitions.addAll(submfa.transitions);
        mfa.transitions.add(t2);

        return mfa;
    }


    private MFA NonCaptureMFA(NonCaptureNode node) {
        MFA mfa = builders(node.child);
        return mfa;
    }

    private MFA BackrefMFA(BackrefNode node) {
        State start = newState();
        State end = newState();
        Transition t = new Transition(start, '\\', end, 
        new Memoryinstruction(node.captureid,VARIABLE));
        
        MFA mfa = new MFA();
        mfa.start = start;
        mfa.end = end;
        mfa.transitions = List.of(t); // Add the transition to the MFA object;
        
        return mfa;
    }
    
    public void printtransitions(MFA mfa) {
        for (Transition t : mfa.transitions) {
            System.out.println(t);
        }
    }
}
