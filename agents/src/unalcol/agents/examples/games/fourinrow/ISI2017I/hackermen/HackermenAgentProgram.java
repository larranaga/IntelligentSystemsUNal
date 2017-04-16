package unalcol.agents.examples.games.fourinrow.ISI2017I.hackermen;

import unalcol.agents.Action;
import unalcol.agents.AgentProgram;
import unalcol.agents.Percept;
import unalcol.agents.examples.games.fourinrow.FourInRow;
import unalcol.agents.examples.games.reversi.Reversi;

import java.util.ArrayList;

/**
 * Created by larra on 15/04/2017.
 */
public class HackermenAgentProgram implements AgentProgram{
    protected String color;
    private static String PASS = "0:0:";
    private String action;
    protected Node root;
    public static int size;
    protected String[][] move;
    private static boolean DEBUG = false;
    private byte player;
    private static final int oo = Integer.MAX_VALUE;

    public HackermenAgentProgram(String color){
        this.color = color;
        player = 1;
        if(this.color == FourInRow.BLACK)
            player = -1;
        PASS = PASS + color;
        init();
    }

    @Override
    public void init() {

    }

    @Override
    public Action compute(Percept p) {
        this.size = Perceptions.SIZE.getIntPerception(p);
        if(move == null) {
            move = new String[this.size][this.size];
            for(int i = 0;i < this.size; i++)
                for(int j = 0; j < this.size; j++)
                    move[i][j] = i +":"+j;
        }
        if (p.getAttribute(Reversi.TURN).equals(color)) {

            size = Integer.parseInt(p.getAttribute(Reversi.SIZE).toString());
            String sTurn = p.getAttribute(Reversi.TURN).toString();

            byte type;
            if (sTurn.equals(Reversi.WHITE))
                type = 1;
            else
                type = -1;
            root = new HackermenAgentProgram.Node(p, type);
            int total = size * size;
            int percentage = 100 * (root.w + root.b) / total;
            int depth = 1, ini = 0;
            if (size <= 10)
                ini = 2;
            else if (size < 16)
                ini = 1;
            if (percentage <= 20)
                depth = ini + 5;
            else if (percentage <= 85)
                depth = ini + 3;
            else
                depth = ini + 5;

            minimax(root, depth, -oo, oo);
            if (root.best == null) {

                return new Action(Reversi.PASS);
            } else {

                if (root.best.x == -1 || root.best.y == -1)
                    return new Action(Reversi.PASS);
                root = root.best;

                return new Action(root.x + ":" + root.y + ":" + color);
            }
        }
        return new Action(PASS);
    }

    private int minimax(Node u, int depth, int alpha, int beta){
        if(depth == 0 || u.w + u.b == size*size)
            return u.profit;
        u.generateOptions();
        int profit = 0;
        if(u.turn == player){
            profit = -oo;
            for(Node e: u.options){
                int tmp = minimax(e, depth-1, alpha, beta);
                alpha = Math.max(alpha, tmp);
                if(profit == -oo || tmp > profit){
                    profit = tmp;
                    u.best = e;
                }
                if(beta <= alpha)break;

            }
        }
        else{
            profit = oo;
            for(Node e: u.options){
                int tmp = minimax(e, depth-1, alpha, beta);
                beta = Math.min(beta, tmp);
                if(profit == oo || tmp < profit){
                    profit = tmp;
                    u.best = e;
                }
                if(beta <= alpha) break;
            }
        }
        return profit;

    }

    private class Node implements Comparable<Node>{
        int profit;
        byte turn;
        int x,y, w, b;
        Node best;
        ArrayList<Node> options;
        byte [][] board;

        public Node(Percept p, byte turn){
            this.turn = turn;
            board =new byte[size][size];
            w = b = 0;
            for(int i = 0; i < size; i++){
                for(int j = 0; j < size; j++){
                    board[i][j] = 0;
                    String s = Perceptions.POSITION.getPositionPerception(p, i, j);
                    if(s.equals(FourInRow.WHITE)){
                        board[i][j] = 1;
                        w++;
                    }
                    else if(s.equals(FourInRow.BLACK)){
                        board[i][j] = -1;
                        b++;
                    }
                }
            }
            x = y= -1;
            profit = 0;
            options = new ArrayList<>();
            best = null;
        }

        public Node(Node other){
            turn = (byte)-other.turn;
            w = other.w;
            b = other.b;
            x=y=-1;
            profit = 0;
            board = other.board.clone();
            options = new ArrayList<>();
            best = null;
        }

        public boolean isValid(int x, int y){
            if (x < 0 || x > size -1 || y < 0 || y > size)
                return false;
            if(board[x][y] != 0)
                return false;
            if(x == size -1)
                return true;
            else if(board[x + 1][y] != 0)
                return true;
            else
                return false;
        }

        public boolean generateOptions(){
            if(options.size() > 0)
                return false;
            Node tmp;
            for(int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    if(isValid(i,j)){
                        tmp = new Node(this);
                        tmp.play(i, j);
                        options.add(tmp);
                    }
                }
            }
            return !options.isEmpty();
        }

        public void play(int x, int y){
            profit = 0;
            this.x = x;
            this.y = y;
            board[x][y] = (byte)turn;
            if(turn == (byte)1)
                w++;
            else
                b++;
            profit = getProfit();
        }

        public int getProfit(){
            byte good = (byte)turn;
            byte bad = (byte)(good == 1 ? -1 : 1);
            int good_fours = getStreak(good, 4);
            int good_threes= getStreak( good, 3);
            int good_twos = getStreak( good, 2);
            int bad_fours = getStreak( bad, 4);
            int bad_threes= getStreak( bad, 3);
            int bad_twos = getStreak( bad, 2);
            if(good_fours > 0)
                return oo;
            if(bad_fours > 0)
                return -oo;
            int ans = (good_threes * 100 + good_twos * 10) - (bad_threes * 100 + bad_twos * 10);
            return ans;
        }

        private int getStreak( byte player, int streak) {
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
                if(row > 0 && col > 0 && col + streak < board[0].length && row + streak < board.length &&
                        board[row - 1][col - 1] ==  board[row + streak][col + streak ] && board[row - 1][col - 1] == (byte)-board[row][col])
                    count = 0;
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
            if(row < board.length-1 && col < board[0].length-1 && col - streak > 0 && row - streak  > 0 &&
                    board[row - streak ][col - streak] ==  board[row + 1][col +1] && board[row + 1][col + 1] == (byte)-board[row][col])
                count = 0;
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
            if(col > 0 && col + streak < board.length && board[row][col-1] == board[row][col + streak] && board[row][col - 1] == (byte)-board[row][col])
                count = 0;
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
            if(row > 0 && row + streak < board.length && board[row -1][col] == board[row + streak][col] && board[row -1][col] == (byte)-board[row][col])
                count = 0;
            return (count == streak ? 1 : 0);
        }

        @Override
        public int compareTo(Node o) {
            return this.profit - o.profit;
        }
    }

}
