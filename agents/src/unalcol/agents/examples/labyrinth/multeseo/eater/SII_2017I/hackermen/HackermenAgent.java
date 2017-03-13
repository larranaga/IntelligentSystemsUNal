package unalcol.agents.examples.labyrinth.multeseo.eater.SII_2017I.hackermen;

import unalcol.agents.Action;
import unalcol.agents.AgentProgram;
import unalcol.agents.Percept;
import unalcol.agents.simulate.util.SimpleLanguage;


import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.PriorityQueue;

/**
 * Created by larra on 10/03/2017.
 */
public class HackermenAgent  implements AgentProgram{

    private boolean DEBUG = false;

    private Deque<Action> actionsQueue;
    private Action lastAction;
    private Board<Node> board;
    private int currentDir;
    private Node currNode;

    public HackermenAgent(SimpleLanguage _language){
        super();
        Actions.language = _language;
        Perceptions.language = _language;
        actionsQueue = new LinkedList<>();
        lastAction = null;
        board = new Board<Node>();
        currentDir = 0;
        currNode = new Node();
    }
    @Override
    public void init() {
        actionsQueue.clear();
        board = new Board<Node>();
        currentDir = 0;
        currNode = new Node();
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

        /*boolean [] adjacent_agent = { Perceptions.AFRONT.getBooleanPerception(p),
                                      Perceptions.ARIGHT.getBooleanPerception(p),
                                      Perceptions.ABACK.getBooleanPerception(p),
                                      Perceptions.ALEFT.getBooleanPerception(p)};
*/
        boolean[] adjacent_agent = {false, false, false, false};
        boolean [] obstacle = {
                wall[0] || adjacent_agent[0],
                wall[1] || adjacent_agent[1],
                wall[2] || adjacent_agent[2],
                wall[3] || adjacent_agent[3]};

        if(DEBUG){
            System.out.println("perceptions: ");
            for (int i = 0; i < obstacle.length; i++) {
                System.out.print(obstacle[i] + " ");
            }
            System.out.println();
        }

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

    private void exploration(boolean[] wall, boolean[] adjacent_agent, boolean [] obstacle, boolean win, boolean fail){
        if(findNearestUnexplored(wall,adjacent_agent,obstacle,win,fail))
            return;
        if(DEBUG) System.out.println("locked");
        rightHandOnWall(wall,adjacent_agent,obstacle,win,fail);
        return;
    }

    private void rightHandOnWall(boolean[] wall, boolean[] adjacent_agent, boolean[] obstacle, boolean win, boolean failed){
        if(!obstacle[1]){
            actionsQueue.add(Actions.ROTATE.getAction());
            actionsQueue.add(Actions.ADVANCE.getAction());
        }
        else if(!obstacle[0]){
            actionsQueue.add(Actions.ADVANCE.getAction());

        }
        else if(!obstacle[3]){
            for (int i = 0; i < 4; i++)
                actionsQueue.add(Actions.ROTATE.getAction());
        }
        else if(!obstacle[2]){
            actionsQueue.add(Actions.ROTATE.getAction());
            actionsQueue.add(Actions.ROTATE.getAction());
            actionsQueue.add(Actions.ADVANCE.getAction());
        }
        else{
            actionsQueue.add(Actions.ROTATE.getAction());
        }
    }

    private boolean findNearestUnexplored(boolean[] wall, boolean[] adjacent_agent, boolean[] obstacle, boolean win, boolean failed) {

        HashMap<Node, Node> parent = new HashMap<Node, Node>();
        HashMap<Node, Integer> absDir = new HashMap<Node, Integer>();
        HashMap<Node, Integer> localDir = new HashMap<Node, Integer>();
        HashMap<Node, Integer> dist = new HashMap<Node, Integer>();

        long currentMillis = System.currentTimeMillis();
        long explDelta, targetval = 0;
        Node target = null;

        for(Node n: board.getBoard().keySet()){
            parent.put(n,null);
            absDir.put(n,-1);
            localDir.put(n,-1);
            dist.put(n,-1);
        }

        parent.put(currNode, currNode);
        absDir.put(currNode, currentDir);
        localDir.put(currNode, 0);
        dist.put(currNode, 0);

        PriorityQueue<Node> pq = new PriorityQueue<Node>(new NodeComp(dist));
        pq.add(currNode);

        while(!pq.isEmpty()){
            Node n = pq.remove();
            Node[] neighbors = n.getNeighbors(absDir.get(n));
            for (int i = 0; i < neighbors.length; i++) {
                if((n == currNode) && obstacle[i]) continue;
                Node m = neighbors[i];
                if(board.isConnected(n,m) && (parent.get(m) == null)){
                    parent.put(m,n);
                    absDir.put(m,(absDir.get(n) + i)%4);
                    localDir.put(m,i);
                    dist.put(m,dist.get(n)+i+1);
                    pq.add(m);

                    explDelta = currentMillis - board.getExplored(m);
                    if(explDelta == currentMillis){
                        if(DEBUG) System.out.println("Target(" +dist.get(m) + "): "+m);
                        route(parent,localDir,m);
                        return true;
                    }
                    if(explDelta > targetval){
                        targetval = explDelta;
                        target = m;
                    }
                }


            }
        }
        parent.put(currNode, null);
        if(DEBUG) System.out.println("Target("+ dist.get(target) +"): "+target);
        route(parent,localDir,target);
        return target != null;

    }

    private void route(HashMap<Node, Node> parent, HashMap<Node, Integer> dir, Node target){
        if(target == null) return;
        int localDir = dir.get(parent.get(target));
        while( target != currNode){
            actionsQueue.addFirst(Actions.ADVANCE.getAction());
            for (int i = 0; i < dir.get(target); i++) {
                actionsQueue.addFirst(Actions.ROTATE.getAction());
                localDir = ++localDir %4 ;
            }
            target = parent.get(target);
        }
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
            Node newNode = currNode.forward(currentDir);
            if(DEBUG){
                System.out.println("Movement : " +currNode + " -("+currentDir + ", "+board.getExplored(newNode) + ")> " + newNode);
            }
            currNode = newNode;
        }
    }
}
