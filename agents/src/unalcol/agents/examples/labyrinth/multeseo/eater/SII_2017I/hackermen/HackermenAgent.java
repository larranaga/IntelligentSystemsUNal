package unalcol.agents.examples.labyrinth.multeseo.eater.SII_2017I.hackermen;

import unalcol.agents.Action;
import unalcol.agents.AgentProgram;
import unalcol.agents.Percept;
import unalcol.agents.simulate.util.SimpleLanguage;


import java.util.Deque;
import java.util.LinkedList;

/**
 * Created by larra on 10/03/2017.
 */
public class HackermenAgent  implements AgentProgram{

    private boolean DEBUG = true;

    private Deque<Action> actionsQueue;
    private Action lastAction;
    private LabyrinthBoard<LabyrinthNode> board;
    private int currentDir;
    private LabyrinthNode currNode;

    public HackermenAgent(SimpleLanguage _language){
        super();
        Actions.language = _language;
        Perceptions.language = _language;
        actionsQueue = new LinkedList<>();
        lastAction = null;
        board = new LabyrinthBoard<LabyrinthNode>();
        currentDir = 0;
        currNode = new LabyrinthNode();
    }
    @Override
    public void init() {
        actionsQueue.clear();
        board = new LabyrinthBoard<LabyrinthNode>();
        currentDir = 0;
        currNode = new LabyrinthNode();
    }

    @Override
    public Action compute(Percept p) {
        boolean win = Perceptions.GOAL_REACHED.getBooleanPerception(p);
        boolean failed = Perceptions.FAIL.getBooleanPerception(p);
        if(win)
            return Actions.NOP.getAction();
        boolean [] wall = { Perceptions.WFRONT.getBooleanPerception(p),
                             Perceptions.WRIGHT.getBooleanPerception(p),
                             Perceptions.WBACK.getBooleanPerception(p),
                             Perceptions.WLEFT.getBooleanPerception(p) };

        boolean [] adjacent_agent = { Perceptions.AFRONT.getBooleanPerception(p),
                                      Perceptions.ARIGHT.getBooleanPerception(p),
                                      Perceptions.ABACK.getBooleanPerception(p),
                                      Perceptions.ABACK.getBooleanPerception(p)};

        boolean [] obstacle = {
                wall[0] || adjacent_agent[0],
                wall[1] || adjacent_agent[1],
                wall[2] || adjacent_agent[2],
                wall[3] || adjacent_agent[3],};

        if(!actionsQueue.isEmpty() && obstacle[0] && Actions.compare(actionsQueue.element(), Actions.ADVANCE.getAction()))
            actionsQueue.clear();

        if(failed)
            actionsQueue.clear();
        updateCurrentPos(failed);
        exploreCurrentPos(wall);

        if(actionsQueue.isEmpty())
            exploration(wall, adjacent_agent, obstacle, win, failed);
        if(actionsQueue.isEmpty())
            return Actions.NOP.getAction();


        return (lastAction = actionsQueue.remove());
    }

    private void exploration(boolean[] wall, boolean[] adjacent_agent, boolean[] obstacle, boolean win, boolean failed) {

        //@TODO
    }

    private void exploreCurrentPos(boolean[] wall){
        for (int i = 0; i < wall.length; i++) {
            if(wall[i]){
                board.addWall(currNode, currNode.forward((currentDir + 1)%4));
            }
            else{
                board.addWay(currNode, currNode.forward(((currentDir + i)%4)));
            }
        }
        board.explore(currNode);
    }



    private void updateCurrentPos( boolean failed ){
        if(lastAction == null)
            return;
        if(failed){
            if(DEBUG){
                System.out.println("Failed "+currNode + "(" + currentDir + ")");
            }
            return;
        }
        if(Actions.compare(lastAction, Actions.ROTATE.getAction())){
            currentDir  = (currentDir + 1)%4;
            if(DEBUG){
                System.out.println("Rotation: to -> " + currentDir);
            }
        }
        if(Actions.compare(lastAction, Actions.ADVANCE.getAction())){
            LabyrinthNode newNode = currNode.forward(currentDir);
            if(DEBUG){
                System.out.println("Movement : " +currNode + " -("+currentDir + ", "+board.getExplored(newNode) + ")> " + newNode);
            }
            currNode = newNode;
        }
    }
}
