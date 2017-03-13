package unalcol.agents.examples.labyrinth.multeseo.eater.SII_2017I.hackermen;


import java.util.HashMap;
/**
 * Created by larra on 11/03/17.
 */
public class Board<N extends Node> {
    private HashMap<N, HashMap<N, Long>> board;
    private HashMap<N, Long> explored;

    public Board(){
        board = new HashMap<N, HashMap<N, Long>>();
        explored = new HashMap<N, Long>();
    }

    public HashMap<N, HashMap<N, Long>> getBoard(){
        return board;
    }

    public HashMap<N, Long> getExplored() {
        return explored;
    }

    public void addWay(N n1, N n2){
        if(!board.containsKey(n1)) board.put(n1, new HashMap<N, Long>());

        board.get(n1).put(n2, System.currentTimeMillis());
        tryExplore(n1);

        if(!board.containsKey(n2))
            board.put(n2,new HashMap<N, Long>());
        board.get(n2).put(n1, System.currentTimeMillis());
        tryExplore(n2);

    }

    public void addWall(N n1, N n2){
        if(!board.containsKey(n1))
            board.put(n1, new HashMap<N, Long>());
        if(board.get(n1).containsKey(n2))
            board.get(n1).remove(n2);

        if(!board.containsKey(n2))
            board.put(n2, new HashMap<N, Long>());
        if(board.get(n2).containsKey(n1))
            board.get(n2).remove(n1);
    }

    public boolean isConnected(N n1, N n2){
        if(!board.containsKey(n1) || !board.containsKey(n2))
            return false;
        return board.get(n1).containsKey(n2) && board.get(n2).containsKey(n1);
    }

    public boolean isExplored(N n){
        return getExplored(n) > 0;
    }

    public long getExplored(N n){
        return explored.containsKey(n) ? explored.get(n) : 0;
    }

    public void tryExplore(N n){
        if(!explored.containsKey(n)){
            explored.put(n,  01L);
        }
    }

    public void explore(N n){
        explored.put(n, System.currentTimeMillis());
    }
}
