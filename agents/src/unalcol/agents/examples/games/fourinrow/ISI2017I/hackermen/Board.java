package unalcol.agents.examples.games.fourinrow.ISI2017I.hackermen;

import unalcol.agents.Percept;
import unalcol.agents.examples.games.fourinrow.FourInRow;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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

    int count(){
        int size = board.length;
        int ans = 0;
        for(int i = 0; i < board.length; i++)
            for(int j = 0; j < board[0].length; j++)
                ans += (board[i][j] != 0 ? 1 : 0);
        return ans;
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

    public boolean isFull(){
        int a = count();
        return (a == board.length * board[0].length);
    }
    public static String swapPlayer(String player){
        return (player.equals(FourInRow.WHITE) ? FourInRow.BLACK : FourInRow.WHITE);
    }

    public boolean validPosition(int x, int y){
        if (x < 0 || x > board.length -1 || y < 0 || y > board[0].length)
            return false;
        if(board[x][y] != 0)
            return false;
        if(x == board.length -1)
            return true;
        else if(board[x + 1][y] != 0)
            return true;
        else
            return false;
    }

    Map<Board, String> getChildren(String player){
        Board b;
        Map<Board, String> ans = new HashMap<>();
        String action;
        for(int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                if(validPosition(i,j)){
                    b = new Board(this);
                    b.setOwner(i,j,player);
                    action = (i + ":" + j + ":" + player);
                    System.out.println(action);
                    ans.put(b, action);
                }
            }
        }
        return ans;
    }

    /**
     * @param player
     * @return heuristic value for current board for player based on
     * (3_lines of player*100 + 2_lines of player*10) - (3_lines of enemy*100 + 2_lines of enemy*10)
     */
    public int eval( String player){
        int good = getOwnerId(player);
        int bad = (good == 1 ? -1 : 1);
        int good_fours = getStreak(good, 4);
        int good_threes= getStreak( good, 3);
        int good_twos = getStreak( good, 2);
        int bad_fours = getStreak( bad, 4);
        int bad_threes= getStreak( bad, 3);
        int bad_twos = getStreak( bad, 2);
        if (good_fours > 0)
            return Integer.MAX_VALUE;
        if(bad_fours > 0)
            return Integer.MIN_VALUE;

        return (good_threes*100 + good_twos*10) - (bad_threes*100 + bad_twos*10);
    }

    /**
     * @param player
     * @param streak
     * @return number of lines of size streak for player player
     */
    private int getStreak( int player, int streak) {
        int count = 0;
        for(int i  = 0; i < board.length; i++){
            for(int j = 0; j < board[0].length; j++){
                if(board[i][j] == player){
                    count += getVerticalStreak(i, j,  streak);
                    count += getHorizontalStreak(i, j, streak);
                    count += getDiagonalStreak(i, j, streak);
                }
            }
        }
        return count;
    }

    private int getDiagonalStreak(int row, int col, int streak) {
        int total = 0;
        int count = 0;
        if(row + streak - 1 < board.length && col + streak - 1 < board[0].length){
            for(int i = 0; i < streak; i++){
                if(board[row][col] == board[row + i][col + i])
                    count++;
                else
                    break;
            }
        }
        total += (count == streak ? 1 : 0);
        count = 0;
        if(row - streak + 1 >=0 && col + streak - 1 < board[0].length){
            for(int i = 0; i < streak; i++){
                if(board[row][col] == board[row - i][col + i])
                    count++;
                else
                    break;
            }
        }
        total += (count == streak ? 1 : 0);
        return total;
    }

    private int getHorizontalStreak(int row, int col, int streak) {
        int count = 0;
        if(col + streak - 1 < board[0].length){
            for(int i = 0; i < streak; i++){
                if(board[row][col] == board[row][col + i])
                    count ++;
                else break;
            }
        }
        return (count == streak ? 1 : 0);
    }

    private int getVerticalStreak(int row, int col, int streak) {
        int count = 0;
        if(row + streak - 1 < board.length){
            for(int i = 0; i < streak; i++){
                if(board[row][col] == board[row + i][col])
                    count ++;
                else break;
            }
        }
        return (count == streak ? 1 : 0);
    }
}
