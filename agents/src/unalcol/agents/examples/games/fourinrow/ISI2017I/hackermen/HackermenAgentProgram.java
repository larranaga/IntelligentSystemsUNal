package unalcol.agents.examples.games.fourinrow.ISI2017I.hackermen;

import unalcol.agents.Action;
import unalcol.agents.AgentProgram;
import unalcol.agents.Percept;
import unalcol.agents.examples.games.fourinrow.FourInRow;

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
    private static boolean DEBUG = true;
    private byte player;
    private static final int oo = Integer.MAX_VALUE;


    public HackermenAgentProgram(String color){
        this.color = color;
        player = 1;
        if(this.color.equals(FourInRow.BLACK))
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
        
        //If it's my turn
        if (Perceptions.TURN.getStringPerception(p).equals(color)) {

            size = Perceptions.SIZE.getIntPerception(p);
            String sTurn = Perceptions.TURN.getStringPerception(p);

            byte type;
            if (sTurn.equals(FourInRow.WHITE))
                type = 1;
            else
                type = -1;
            root = new HackermenAgentProgram.Node(p, type);
            /*if(DEBUG)
                System.out.println("board i see is:" + root.toString());*/
            int total = size * size;
            int percentage = 100 * (root.w + root.b) / total; //Non-empty tiles percentage
            int depth = 1, ini = 0; //<--
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
                return new Action(PASS);
            } else {
                if (root.best.x == -1 || root.best.y == -1)
                    return new Action(PASS);
                root = root.best;
                return new Action(root.x + ":" + root.y + ":" + color);
            }
        }
        return new Action(PASS);
    }

    private int minimax(Node u, int depth, int alpha, int beta){
        if(depth == 0 || u.w + u.b == size*size || u.winState)
            return u.profit;
        u.generateOptions();
        int profit = 0;
        /*if(DEBUG)
            System.out.println("parent node: " + u.toString());
           */
        if(u.turn == player){
            profit = -oo;
            /*if(DEBUG)
                System.out.println("max turn");*/
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
        int x,y, w, b; //w,b: number of whites/blacks
        Node best;
        ArrayList<Node> options;
        byte [][] board;
        boolean winState;

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
            profit = (this.board == null ? 0: getProfit());
            this.winState = false;
            options = new ArrayList<>();
            best = null;
        }

        public Node(Node other){
            turn = (byte)-other.turn;
            w = other.w;
            this.winState = false;
            b = other.b;
            x=y=-1;
            profit = (this.board == null ? 0 :getProfit());
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
            Node tmp = new Node(this);
            for(int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    if(tmp.isValid(i,j)){
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

        //Returns profit suming up the values of each 2-row and 3-row. 2-rows have a value of 10 and 3-rows have a value of 100
        public int getProfit(){
            byte good = turn;
            byte bad = (byte)(good == 1 ? -1 : 1);
            int good_fours = getStreak(good, 4);
            int good_threes= getStreak( good, 3);
            int good_twos = getStreak( good, 2);
            int bad_fours = getStreak( bad, 4);
            int bad_threes= getStreak( bad, 3);
            int bad_twos = getStreak( bad, 2);
            if(DEBUG) {
                System.out.println(this);
                System.out.println("getting profit");
                System.out.println("good fours = " + good_fours);
                System.out.println("good threes = " + good_threes);
                System.out.println("good twos = " + good_twos);
                System.out.println("bad fours = " + bad_fours);
                System.out.println("bad threes = " + bad_threes);
                System.out.println("bad twos = " + bad_twos);
            }
            
            //I won!! :)
            if(good_fours > 0) {
                this.winState = true;
                return oo;
            }
            
            //I lost :(
            if(bad_fours > 0) {
                this.winState = true;
                return -oo;
            }
            int ans = (good_threes * 100 + good_twos * 10) - (bad_threes * 100 + bad_twos * 10);
            if(DEBUG)
                System.out.println("total profit: " + ans);
            return ans;
        }

        //Tells how many streaks of a given size and a given color are in the board
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

        //Tells whether there is a streak of n tiles long in "the sight" of a specified tile returning 1 if so and 0 otherwise
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

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("");
            sb.append("Number of whites: ").append(this.w).append(" ");
            sb.append("Number of blacks: ").append(this.b).append("\n");
            sb.append("Turn of: ").append(this.turn == 1 ? "white" : "black").append("\n");
            sb.append("profit: ").append(this.profit).append("\n");
            for(byte[] x: this.board){
                for (byte aX : x) {
                    if(aX == 1)
                        sb.append("w");
                    else if (aX == 0)
                        sb.append(".");
                    else if (aX == -1)
                        sb.append("b");
                    sb.append(" ");
                }
                sb.append("\n");
            }
            return sb.toString();
        }
    }

}
