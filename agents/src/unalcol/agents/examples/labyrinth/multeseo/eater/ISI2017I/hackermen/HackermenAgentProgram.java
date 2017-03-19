package unalcol.agents.examples.labyrinth.multeseo.eater.ISI2017I.hackermen;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.PriorityQueue;
import unalcol.agents.Action;
import unalcol.agents.AgentProgram;
import unalcol.agents.Percept;
import unalcol.agents.simulate.util.SimpleLanguage;

/**
 * Created by larra on 13/03/17.
 */
public class HackermenAgentProgram implements AgentProgram{
    static boolean DEBUG = false;

    private Deque<Action> actionsQueue;
    private Action lastAction;
    private Board<Node> board;
    private int currentDir;
    private Node currNode;

    public HackermenAgentProgram(SimpleLanguage language){
        super();
        Actions.language = language;
        Perceptions.language = language;
        actionsQueue = new LinkedList<Action>();
        lastAction = null;
        board = new Board<Node>();
        currentDir = 0;
        currNode = new Node();
    }

    @Override
    public void init(){
        actionsQueue.clear();
        board = new Board<Node>();
        currentDir = 0;
        currNode = new Node(0, 0);
    }

    @Override
    public Action compute(Percept p){
        boolean win = Perceptions.GOAL_REACHED.getBooleanPerception(p);
        boolean fail = Perceptions.FAIL.getBooleanPerception(p);

        if (win)
            return Actions.NOP.getAction();

        boolean[] wall={Perceptions.WFRONT.getBooleanPerception(p),
                        Perceptions.WRIGHT.getBooleanPerception(p),
                        Perceptions.WBACK.getBooleanPerception(p),
                        Perceptions.WLEFT.getBooleanPerception(p) };



        boolean[] adjacent_agent = //{ false, false, false, false };
                {       Perceptions.AFRONT.getBooleanPerception(p),
                        Perceptions.ARIGHT.getBooleanPerception(p),
                        Perceptions.ABACK.getBooleanPerception(p),
                        Perceptions.ALEFT.getBooleanPerception(p) };


        boolean[] obstacle = { wall[0] || adjacent_agent[0],
                        wall[1] || adjacent_agent[1],
                        wall[2] || adjacent_agent[2],
                        wall[3] || adjacent_agent[3] };

        if (!actionsQueue.isEmpty() && obstacle[0] && Actions.compare(actionsQueue.element(), Actions.ADVANCE.getAction()))
            actionsQueue.clear();

        if (fail)
            actionsQueue.clear();

        updateCurrentPos(fail);
        exploreCurrentPos(wall);

        if (actionsQueue.isEmpty())
            exploration(wall, adjacent_agent, obstacle, win, fail);

        if (actionsQueue.isEmpty())
            return Actions.NOP.getAction();
        return (lastAction = actionsQueue.remove());
    }

    private void exploration(boolean[] wall, boolean[] adjacent_agent, boolean[] obstacle, boolean win, boolean fail){
        if (findNearestUnexplored(wall, adjacent_agent, obstacle, win, fail))
            return;
        if (DEBUG)
            System.out.println("ERROR (Encerrado)");

        rightHandOnWall(obstacle);
        return;
    }

    private boolean findNearestUnexplored(boolean[] wall, boolean[] adjacent_agent, boolean[] obstacle, boolean win, boolean fail){
        HashMap<Node, Node> parent = new HashMap<Node, Node>();
        HashMap<Node, Integer> absDir = new HashMap<Node, Integer>();
        HashMap<Node, Integer> localDir = new HashMap<Node, Integer>();
        HashMap<Node, Integer> dist = new HashMap<Node, Integer>();

        long currentTimeMillis = System.currentTimeMillis();
        long exploredDelta;
        long targetVal = 0;
        Node target = null;

        for (Node n : board.getBoard().keySet()){
            parent.put(n, null);
            absDir.put(n, -1);
            localDir.put(n, -1);
            dist.put(n, -1);
        }

        parent.put(currNode, currNode);
        absDir.put(currNode, currentDir);
        localDir.put(currNode, 0);
        dist.put(currNode, 0);

        PriorityQueue<Node> queue = new PriorityQueue<Node>(new NodeComp(dist));
        queue.add(currNode);

        while (!queue.isEmpty()){
            Node n = queue.remove();
            Node[] neighbors = n.getNeighbors(absDir.get(n));
            for (int i = 0; i < neighbors.length; i++){
                if ((n == currNode) && obstacle[i]) continue;

                Node m = neighbors[i];
                if (board.isConnected(n, m) && (parent.get(m) == null)){
                    parent.put(m, n);
                    absDir.put(m, (absDir.get(n) + i) % 4);
                    localDir.put(m, i);
                    dist.put(m, dist.get(n) + i + 1);
                    queue.add(m);

                    exploredDelta = currentTimeMillis - board.getExplored(m);
                    if (exploredDelta == currentTimeMillis){
                        if (DEBUG)
                            System.out.println("Target(" + dist.get(m) + "): " + m);

                        parentRoute(parent, localDir, m);
                        return true;
                    }
                    if (exploredDelta > targetVal){
                        targetVal = exploredDelta;
                        target = m;
                    }
                }
            }
        }
        parent.put(currNode, null);

        if (DEBUG)
            System.out.println("Target(" + dist.get(target) + "): " + target);

        parentRoute(parent, localDir, target);
        return target != null;
    }

    private void parentRoute(HashMap<Node, Node> parent, HashMap<Node, Integer> dir, Node target){
        if (target == null)
            return;
        int localDir = dir.get(parent.get(target));
        while (target != currNode){
            actionsQueue.addFirst(Actions.ADVANCE.getAction());
            for (int i = 0; i < dir.get(target); i++){
                actionsQueue.addFirst(Actions.ROTATE.getAction());
                localDir = ++localDir % 4;
            }
            target = parent.get(target);
        }
    }

    private void rightHandOnWall(boolean[] obstacle ){
        for (int i = 0; i < 4; i++) {
            if(!obstacle[i]){
                for (int j = 0; j < i; j++)
                    actionsQueue.add(Actions.ROTATE.getAction());
                actionsQueue.add(Actions.ADVANCE.getAction());
                return;
            }
        }
    }

    private void exploreCurrentPos(boolean[] wall){
        for (int i = 0; i < wall.length; i++){
            if (wall[i]){
                board.addWall(currNode, currNode.forward((currentDir + i) % 4));
            }
            else{
                board.addWay(currNode, currNode.forward((currentDir + i) % 4));
            }
        }
        board.explore(currNode);
    }

    private void updateCurrentPos(boolean fail){
        if (lastAction == null)
            return;
        if (fail){
            if (DEBUG)
                System.out.println("Fail: " + currNode + "(" + currentDir + ")");
            return;
        }
        if (Actions.compare(lastAction, Actions.ROTATE.getAction())){
            currentDir = (currentDir + 1) % 4;
            if (DEBUG)
                System.out.println("Rotation: to -> " + currentDir);
        }
        if (Actions.compare(lastAction, Actions.ADVANCE.getAction())){
            Node newPos = currNode.forward(currentDir);
            if (DEBUG)
                System.out.println("Movement : " + currNode + " -(" + currentDir + ", " + board.getExplored(newPos) + ")> " + newPos);
            currNode = newPos;
        }
    }
}