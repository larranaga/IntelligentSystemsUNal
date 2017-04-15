package unalcol.agents.examples.games.fourinrow.ISI2017I.hackermen;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by larra on 14/04/2017.
 */
public class GameTree {
    private Map<Board, Map<Board, String>> gameTree;
    private Map<Board, Map<Board, String>> enemyTree;
    private Map<Board, Board> bestChild;
    private Board root;
    private String player;
    private int depth;
    private Map<Board, Integer> value;
    private Map<Board, Board> maxPruning;
    private Map<Board, Board> minPruning;
    private static final int MAX_DEPTH = 6;
    private static boolean DEBUG = true;

    public GameTree(Board root, String player){
        super();
        gameTree = new HashMap<>();
        enemyTree = new HashMap<>();
        bestChild = new HashMap<>();
        this.root = root;
        this.player = player;
        depth = 0;
        value = new HashMap<>();
        maxPruning = new HashMap<>();
        minPruning = new HashMap<>();
    }

    public void setRoot(Board root){
        this.root = root;
        depth = 0;
    }

    public String get(Board b1, Board b2)
    {
        if (gameTree.containsKey(b1) && gameTree.get(b1).containsKey(b2))
            return gameTree.get(b1).get(b2);
        return null;
    }

    public String getBestMove()
    {
        if(DEBUG)
            System.out.println("this.root \n" + this.root);
        if (bestChild.containsKey(this.root))
            return get(this.root, bestChild.get(this.root));
        return null;
    }

    public Board getBestChild(Board state)
    {
        if (bestChild.containsKey(state))
            return bestChild.get(state);
        return null;
    }

    public int getValue(Board board)
    {
        return value.get(board);
    }

    public void explore(){
        alphabeta(this.root, ++depth, Integer.MIN_VALUE, Integer.MAX_VALUE, true);
    }

    private int alphabeta(Board currentRoot, int depth, int a, int b, boolean maximize)
    {
        if ((depth > MAX_DEPTH) || currentRoot.isFull())
            return currentRoot.eval(player);
        if (maximize)
        {
            int bestVal = Integer.MIN_VALUE;
            Board bestBoard = null;
            if (!gameTree.containsKey(currentRoot))
            {
                gameTree.put(currentRoot, currentRoot.getChildren(player));
            }
            Set<Board> children = new LinkedHashSet<Board>();
            if (maxPruning.containsKey(currentRoot))
            {
                children.add(maxPruning.get(currentRoot));
            }
            children.addAll(gameTree.get(currentRoot).keySet());
            for (Board child : children)
            {
                int childVal = alphabeta(child, depth + 1, a, b, false);
                if (childVal > bestVal)
                {
                    bestVal = childVal;
                    bestBoard = child;
                }
                if (bestVal > a)
                {
                    a = bestVal;
                }
                if (b <= a)
                {
                    maxPruning.put(currentRoot, child);
                    break;
                }
            }
            bestChild.put(currentRoot, bestBoard);
            value.put(currentRoot, bestVal);
            return bestVal;
        }
        else
        {
            int bestVal = Integer.MAX_VALUE;
            if (!enemyTree.containsKey(currentRoot))
            {
                enemyTree.put(currentRoot, currentRoot.getChildren(Board.swapPlayer(player)));
            }
            Set<Board> children = new LinkedHashSet<Board>();
            if (minPruning.containsKey(currentRoot))
            {
                children.add(minPruning.get(currentRoot));
            }
            children.addAll(enemyTree.get(currentRoot).keySet());
            for (Board child : children)
            {
                int newVal = alphabeta(child, depth + 1, a, b, true);
                if (newVal < bestVal)
                {
                    bestVal = newVal;
                }
                if (bestVal < b)
                {
                    b = bestVal;
                }
                if (b <= a)
                {
                    minPruning.put(currentRoot, child);
                    break;
                }
            }
            return bestVal;
        }
    }
}
