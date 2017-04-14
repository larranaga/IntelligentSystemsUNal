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
    private Explorer explorer;
    private static final int MAX_DEPTH = 6;

    public GameTree(Board _root, String _player){
        super();
        gameTree = new HashMap<>();
        enemyTree = new HashMap<>();
        bestChild = new HashMap<>();
        root = _root;
        player = _player;
        depth = 0;
        value = new HashMap<>();
        maxPruning = new HashMap<>();
        minPruning = new HashMap<>();
    }

    public void setRoot(Board _root){
        stopExplorer();
        root = _root;
        depth = 0;
        startExplorer();
    }

    @SuppressWarnings("deprecation")
    public void stopExplorer(){
        if(explorer != null){
            explorer.stop();
            explorer = null;
        }
    }

    public void startExplorer(){
        stopExplorer();
        explorer = new Explorer();
        explorer.start();
    }

    public String get(Board b1, Board b2)
    {
        if (gameTree.containsKey(b1) && gameTree.get(b1).containsKey(b2))
            return gameTree.get(b1).get(b2);
        return null;
    }

    public String getBestMove(Board state)
    {
        if (bestChild.containsKey(state))
            return get(state, bestChild.get(state));
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



    public int increaseDepth(){
        alpha_beta(root, ++depth, Integer.MIN_VALUE, Integer.MAX_VALUE, true);
        return depth;
    }

    private int alpha_beta(Board root, int depth, int a, int b, boolean maximize){
        if((depth == 0) || root.isFull())
            return root.eval(player);
        if(maximize){
            int bestVal = Integer.MIN_VALUE;
            Board bestBoard = null;
            if(!gameTree.containsKey(root))
                gameTree.put(root, root.getChildren(player));
            Set<Board> children = new LinkedHashSet<>();
            if(maxPruning.containsKey(root))
                children.add(maxPruning.get(root));
            children.addAll(enemyTree.get(root).keySet());
            for(Board child : children){
                int newVal = alpha_beta(child, depth - 1, a, b, false);
                if(newVal > bestVal){
                    bestVal = newVal;
                    bestBoard = child;
                }
                a = Math.max(a, bestVal);
                if(b <= a){
                    maxPruning.put(root, child);
                    break;
                }
            }
            bestChild.put(root, bestBoard);
            value.put(root, bestVal);
            return bestVal;
        }
        else{
            int bestVal = Integer.MAX_VALUE;
            if(!enemyTree.containsKey(root))
                enemyTree.put(root, root.getChildren(Board.swapPlayer(player)));
            Set<Board> children = new LinkedHashSet<>();
            if(minPruning.containsKey(root))
                children.add(minPruning.get(root));
            children.addAll(enemyTree.get(root).keySet());
            for(Board child: children){
                int newVal = alpha_beta(child, depth-1, a, b, true);
                bestVal = Math.min(bestVal, newVal );
                b = Math.min(b, bestVal);
                if(b <=a){
                    minPruning.put(root, child);
                    break;
                }
            }
            return bestVal;
        }
    }

    private class Explorer extends Thread{
        @Override
        public void run() {
            while(depth < MAX_DEPTH){
                increaseDepth();
            }
        }
    }
}
