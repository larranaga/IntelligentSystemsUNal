/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package unalcol.agents.examples.games.reversi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

import unalcol.agents.Action;
import unalcol.agents.AgentProgram;
import unalcol.agents.Percept;

/**
 *
 * @author Hommies
 */
public class ReversiAgentHommies implements AgentProgram {

	final static int DD = 8;
	final static int[] DX = { 0, 0, 1, -1, 1, 1, -1, -1 };
	final static int[] DY = { 1, -1, 0, 0, 1, -1, 1, -1 };

	final static int oo = Integer.MAX_VALUE;

	protected String color;
	protected Node root;
	public static int size;
	public byte player;
	protected String[][] t;

	public ReversiAgentHommies(String color) {
		this.color = color;
		player = 1;
		if (color.equals(Reversi.BLACK))
			player = -1;
	}

	@Override
	public Action compute(Percept p) {

		this.size = Integer.parseInt(p.getAttribute(Reversi.SIZE).toString());
		if (t == null) {
			t = new String[size][size];
			for (int i = 0; i < size; i++)
				for (int j = 0; j < size; j++)
					t[i][j] = i + ":" + j;
		}
		if (p.getAttribute(Reversi.TURN).equals(color)) {

			size = Integer.parseInt(p.getAttribute(Reversi.SIZE).toString());
			String sTurn = p.getAttribute(Reversi.TURN).toString();

			byte type;
			if (sTurn.equals(Reversi.WHITE))
				type = 1;
			else
				type = -1;
			root = new Node(p, type);
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

		return new Action(Reversi.PASS);
	}


	public int minimax(Node u, int depth, int alpha, int beta) {
		if (depth == 0 || u.w + u.b == size * size)
			return u.profit;
		u.generateOptions();

		int profit = 0;
		if (u.turn == player) {

			profit = -oo;
			for (Node e : u.options) {
				int tmp = minimax(e, depth - 1, alpha, beta);
				alpha = Math.max(alpha, tmp);

				if (profit == -oo || tmp > profit) {
					profit = tmp;

					u.best = e;
				}
				if (beta <= alpha) {

					break;
				}
			}
		} else {

			profit = oo;
			for (Node e : u.options) {
				int tmp = minimax(e, depth - 1, alpha, beta);
				beta = Math.min(beta, tmp);
				if (profit == oo || tmp < profit) {
					profit = tmp;
					u.best = e;
				}
				if (beta <= alpha) {
					// System.out.println("Hice poda!!!!
					// :)"+(u.options.size()-cont));
					break;
				}
			}
		}

		return profit;
	}

	class Node implements Comparable<Node> {

		byte turn;
		int w, b, x, y, profit;
		byte[] board;
		ArrayList<Node> options;
		Node best;

		public Node(Percept p, byte turn) {

			this.turn = turn;
			board = new byte[size * size];
			w = b = 0;
			for (int i = 0; i < size; i++) {
				for (int j = 0; j < size; j++) {
					board[i * size + j] = 0;
					String cell = p.getAttribute(i + ":" + j).toString();
					if (cell.equals(Reversi.WHITE)) {
						board[i * size + j] = 1;
						w++;
					} else if (cell.equals(Reversi.BLACK)) {
						board[i * size + j] = -1;
						b++;
					}
				}
			}
			x = y = -1;
			profit = b - w;
			if (turn == 1)
				profit = w - b;
			options = new ArrayList<>();
			best = null;
		}

		public Node(Node other) {
			turn = (byte) -other.turn;
			w = other.w;
			b = other.b;
			x = y = -1;
			profit = b - w;
			if (turn == 1)
				profit = w - b;
			board = other.board.clone();
			options = new ArrayList<>();
			best = null;
		}

		public boolean isValid(int x, int y) {
			return (0 <= x && x < size && 0 <= y && y < size);
		}

		public boolean generateOptions() {
			if (options.size() > 0)
				return false;
			Node tmp;
			boolean[][] used = new boolean[size][size];
			for (int i = 0; i < size; i++) {
				for (int j = 0; j < size; j++) {
					if (-turn == board[i * size + j]) {
						int ni, nj;
						for (int k = 0; k < DD; k++) {
							ni = i + DX[k];
							nj = j + DY[k];
							if (isValid(ni, nj) && !used[ni][nj] && board[ni * size + nj] == 0) {
								used[ni][nj] = true;
								tmp = new Node(this);
								tmp.turn = (byte) (-tmp.turn);
								boolean ok = tmp.updateBoard(ni, nj);
								tmp.turn = (byte) (-tmp.turn);
								if (ok)
									options.add(tmp);
							}
						}
					}
				}
			}
			return !options.isEmpty();
		}

		public boolean updateBoard(int x, int y) {
			profit = 0;
			this.x = x;
			this.y = y;
			board[x * size + y] = (byte) turn;
			boolean ok = false;
			for (int k = 0; k < DD; k++) {
				int dx = DX[k], dy = DY[k];
				int nx = x + dx, ny = y + dy, cnt = 0;
				while (isValid(nx, ny) && board[nx * size + ny] == (byte) (-turn)) {
					board[nx * size + ny] = (byte) turn;
					nx += dx;
					ny += dy;
					cnt++;
				}
				if (isValid(nx, ny) && board[nx * size + ny] == (byte) turn) {
					if (turn == 1) {
						w += cnt;
						b -= cnt;
					} else {
						w -= cnt;
						b += cnt;
					}
					if (cnt > 0)
						ok = true;
				} else {
					nx = x + dx;
					ny = y + dy;
					while (cnt-- > 0) {
						board[nx * size + ny] = (byte) (-turn);
						nx += dx;
						ny += dy;
					}
				}
			}
			if (ok) {
				if (turn == 1)
					profit = (++w) - b;
				// profit = ++w;
				else
					profit = (++b) - w;
				// profit = ++b;
				profit += specialProfit();
			}
			return ok;
		}

		public int specialProfit() {
			if ((x == 0 && (y == 0 || y == size - 1)) || (x == size - 1 && (y == 0 || y == size - 1))) {

				return 1000;
			}
			if (x == 0 || x == size - 1 || y == 0 || y == size - 1) {

				return 100;
			}
			return 0;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("profit: " + profit + "w: " + w + " b: " + b + "\n");
			System.out.println("Turn: " + turn);
			System.out.println("Move: " + x + ", " + y);
			System.out.println("Profit: " + profit);
			for (int i = 0; i < size; i++) {
				for (int j = 0; j < size; j++) {
					if (board[i * size + j] != -1)
						sb.append(" ");
					sb.append(board[i * size + j] + " ");
				}
				sb.append("\n");
			}
			return sb.toString();
		}

		@Override
		public int compareTo(Node o) {
			return o.profit - profit;
		}
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

}
