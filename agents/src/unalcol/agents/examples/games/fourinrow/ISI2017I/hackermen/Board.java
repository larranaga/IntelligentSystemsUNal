package unalcol.agents.examples.games.fourinrow.ISI2017I.hackermen;

import unalcol.agents.Percept;
import unalcol.agents.examples.games.fourinrow.FourInRow;

import java.util.Arrays;

/**
 * Created by larra on 12/04/2017.
 */
public class Board {
    protected int[][] board;

    public Board(int size){
        board = new int[size][size];
        for(int i = 0; i < size; i++)
            Arrays.fill(board[i], 0);
    }

    public Board(Percept p){
        int size = Perceptions.SIZE.getIntPerception(p);
        board = new int[size][size];
        for(int i = 0; i < size; i++){
            for(int j = 0; j < size; j++){
                board[i][j] = Perceptions.POSITION.getPositionPerception(p, i, j);
            }
        }
    }

    public Board(Board b){
        int size = b.board.length;
        board = new int[size][size];
        for(int i = 0; i < size; i++)
            for(int j = 0; j < size; j++)
                board[i][j] = b.board[i][j];
    }

    public int getOwner(int x, int y){
        return board[x][y];
    }

    public static int getOwnerId(String player){
        if(player.equals(FourInRow.WHITE))
            return 1;
        else if(player.equals(FourInRow.BLACK))
            return -1;
        else
            return 0;
    }

    public boolean isOwner(int x, int y, String player){
        return board[x][y] == getOwnerId(player);
    }

    public void setOwner(int x, int y, String player){
        board[x][y] = getOwnerId(player);
    }
}
