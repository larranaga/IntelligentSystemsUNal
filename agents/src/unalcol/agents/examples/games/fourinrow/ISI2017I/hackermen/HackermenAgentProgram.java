package unalcol.agents.examples.games.fourinrow.ISI2017I.hackermen;

import unalcol.agents.Action;
import unalcol.agents.AgentProgram;
import unalcol.agents.Percept;
import unalcol.agents.examples.games.fourinrow.FourInRow;

import java.rmi.server.ExportException;

/**
 * Created by larra on 14/04/2017.
 */
public class HackermenAgentProgram implements AgentProgram{
    private static final int WAIT_TIME = 1001;
    private static final int DEFAULT_SIZE = 8;
    private GameTree gameTree;
    protected String color;
    private static String PASS = "0:0:";
    private static boolean DEBUG = true;

    public HackermenAgentProgram(String color){
        this.color = color;
        PASS = PASS + color;
        init();
    }
    @Override
    public void init() {
        gameTree = new GameTree(new Board(DEFAULT_SIZE), color);
    }

    @Override
    public Action compute(Percept p) {
        try{
            Thread.sleep(WAIT_TIME);
        }
        catch (Exception e){}
        if(Perceptions.TURN.getStringPerception(p).equals(color)) {
            Board b = new Board(p);
            if (b.isFull())
                return new Action(PASS);
            gameTree.setRoot(b);
            String action;
            gameTree.explore();
            action = gameTree.getBestMove();
            if (DEBUG)
                System.out.println("action = " + action);
            return new Action(action);
        }
        return new Action(PASS);
    }
}
