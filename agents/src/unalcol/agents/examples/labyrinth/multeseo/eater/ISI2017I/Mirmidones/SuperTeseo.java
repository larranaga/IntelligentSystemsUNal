package unalcol.agents.examples.labyrinth.multeseo.eater.ISI2017I.Mirmidones;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import unalcol.agents.simulate.util.SimpleLanguage;
import unalcol.types.collection.list.Queue;
import unalcol.types.collection.list.Stack;

public class SuperTeseo extends MultiTeseoAgentProgram {
	int MOVES[][] = new int[][]{{1, 0}, {0, 1}, {-1, 0}, {0, -1}};
	
	HashMap<Position, HashSet<Position>> graph;
	HashMap<Position, Integer> back;
	Stack<Integer> wayBackHome;
	HashSet<Position> runBitchRun; 
	Position now;
	int head;
	Position target;
	int bad;
	
	
	public SuperTeseo(){ }
	
	public SuperTeseo(   SimpleLanguage _language  ) {
	  	  super(_language);
	  	  initMem();
    }
	
	public void initMem( ){
		graph = new HashMap<>();
		back = new HashMap<>();
		wayBackHome = new Stack<>();
		runBitchRun = new HashSet<>();
		now = new Position(0,0);
		head = 0;
		bad = 0;
	}
	
	public Position makeMove( Position position, int direction ){
		return new Position(position.getX() + MOVES[direction][0], position.getY() + MOVES[direction][1]);
	}
	
	public int inverseMove( int direction ){
		return (direction + 2) % 4;
	}
	
	public Integer initFork(Boolean[] walls){
		int forks = 0;
		for (int i = 0; i < walls.length; i++) {
			int headPerhaps = (head + i) % 4;
	    	Position nowPerhaps  = makeMove(now, headPerhaps);
			if( !walls[i] && !graph.containsKey(nowPerhaps))
				forks++;
		}
		return forks;
		
	}
	
	public int getDirection(Position init, Position end){
		int hor = end.getX() - init.getX();
		int ver = end.getY() - init.getY();
		if( hor != 0 ) return hor == 1? 2:0;
		if( ver != 0 ) return ver == 1? 3:1;
		return -1;
	}
	
	
	@Override
    public int accion(boolean PF, boolean PD, boolean PA, boolean PI, boolean MT, boolean FAIL, 
				boolean AF, boolean AD, boolean AA, boolean AI) {
    	
        if (MT) return -1;
        boolean flag = true;
        int k=-1;
        Boolean[] walls = new Boolean[]{PF, PD, PA, PI};
        
        Boolean[] agents = new Boolean[]{AF, AD, AA, AI};

        if( !graph.containsKey(now) )
        	graph.put(now, new HashSet<>());
        else bad ++;
        
        for (int i = 0; i < walls.length; i++) {
        	int headPerhaps = (head + i) % 4;
        	Position nowPerhaps  = makeMove(now, headPerhaps);
        	if( !walls[i] && back.containsKey(nowPerhaps) && !graph.get(now).contains(nowPerhaps) ){
				if(back.get(nowPerhaps) >= 1)
					back.put(nowPerhaps, back.get(nowPerhaps) - 1);
				if(back.get(nowPerhaps) < 1)
					back.remove(nowPerhaps);
        	}
        	if( !walls[i] && graph.containsKey(nowPerhaps) ){
        		graph.get(now).add(nowPerhaps);
        		graph.get(nowPerhaps).add(now);
        	}

    	}
        //System.out.println(bad);
        /*
        for (Position asd : graph.keySet()) {
        	System.out.println("cur:");
			System.out.println(asd.toSring());
			System.out.println("childs:");
			for( Position amd : graph.get(asd) )
				System.out.println(amd.toSring());
		}
        System.out.println();
        */
        if( !back.containsKey(now) ){
        	int count = 0;
        	for( int i=0; i<4; i++ )
        		if( agents[i] )
        			count++;
        	if( count == 0 ){
	        	Integer branches = initFork(walls);
	        	if(branches > 1)
	        		back.put(now, branches);
        	}
    	}
        
        
        /*
        for (Position asd : back.keySet()) {
        	System.out.println("cur:");
			System.out.println(asd.toSring());
			System.out.println("forks:");
				System.out.println(back.get(asd));
		}
        System.out.println();
        /*
        System.out.println("cosaas");
		for (Position coso : back) {
			System.out.println("hola " + coso.toSring());
		}*/
        
        while( flag ){
        	int countAgents = 0;
        	int countWalls = 0;
            for (int i = 0; i < walls.length; i++) {
            	if( agents[i] ) countAgents++;
            	if( walls[i] ) countWalls++; 
            	int headPerhaps = (head + i) % 4;
            	Position nowPerhaps  = makeMove(now, headPerhaps);
        		if( !walls[i] && !graph.containsKey(nowPerhaps) && !agents[i]){
            		now = nowPerhaps;
            		head = headPerhaps;
					k = i;
					flag = walls[i];
					break;
            	}
        	}
            if(k == -1){
            	if( countAgents == 1 && countWalls == 3  )
            		return -1;
            	else if( countAgents == 1 && countWalls == 2 )
            		runBitchRun.add(now);
            	if( wayBackHome.size() == 0 ){
            		wayBackHome = solve(now, head);
	            	
            	}
        		int backHead = wayBackHome.pop(); 
        		int turns = (backHead - head) % 4;
            	if(turns < 0) turns += 4;
        		k = turns;
        		now = makeMove(now, backHead);
        		head = backHead;
        		if(now.equals(target))
        			target = null;
        		
        		//System.out.println("head" + head);
        	
            	flag = walls[k];
            	break;
            	
            }
            
            
        }
        try {
			Thread.sleep(0);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return k;
    }
	
	public int ManhattanHeuristic( Position source ){
		return Math.abs(source.getX() - target.getX()) + Math.abs(source.getY() - target.getY());
	}
	
	public HashMap<Position, Position> BFS( Position source ){
		Queue<Position> queue = new Queue<>();
		HashSet<Position> visited = new HashSet<>();
		HashMap<Position, Position> parent = new HashMap<>();
		parent.put(source, null);
		queue.add(source);
		visited.add(source);
		while( queue.size() != 0 ){
			Position current = queue.get();
			queue.del();
			for (Position neighbor : graph.get(current)) {
				if( back.containsKey(neighbor) ){
					parent.put(neighbor, current);
					target = neighbor;
					//back.remove(neighbor);
					return parent;
				}
				if( !visited.contains(neighbor) ){
					queue.add(neighbor);
					parent.put(neighbor, current);
					visited.add(neighbor);
				}
			}
		}
		return null;
	}
	
	public HashMap<Position, Position> BFS1( Position source ){
		Queue<Position> queue = new Queue<>();
		HashSet<Position> visited = new HashSet<>();
		HashMap<Position, Position> parent = new HashMap<>();
		parent.put(source, null);
		queue.add(source);
		visited.add(source);
		while( queue.size() != 0 ){
			Position current = queue.get();
			queue.del();
			for (Position neighbor : graph.get(current)) {
				if( runBitchRun.contains(neighbor) ){
					runBitchRun.remove(neighbor);
					parent.put(neighbor, current);
					target = neighbor;
					//back.remove(neighbor);
					return parent;
				}
				if( !visited.contains(neighbor) ){
					queue.add(neighbor);
					parent.put(neighbor, current);
					visited.add(neighbor);
				}
			}
		}
		return null;
	}
	
	public Stack<Integer> solve( Position source, int head ){
		HashMap<Position, Position> parent = BFS(source);
		if( parent == null )
		{
			parent = BFS1(source);
		}
		/*for (Position ch : parent.keySet()) {
			if( ch == null ) System.out.println(ch);
			String aux = parent.get(ch) == null?"None":parent.get(ch).toSring();
			System.out.println(ch.toSring() + ":" + aux);
			
		}*/
		Stack<Integer> way = new Stack<>();
		Position cur = target;
		while( cur != null ){
			if( parent.get(cur) != null )
				way.push( getDirection( cur, parent.get(cur) ) );
			cur = parent.get(cur);
		}
		/*
		for( Position child = target; child != null; child = parent.get(child) ){
			if( parent.get(child) != null ){
				int direction = getDirection( child, parent.get(child) );
				int turns = (direction - head) % 4;
	        	if(turns < 0) turns += 4;
				way.add(turns);
			}
		}*/
		return way;
	}
	
	class Position{
		int x, y;
		public Position( int x, int y ){
			this.x = x;
			this.y = y;
		}
		public int getX( ){
			return x;
		}
		
		public int getY( ){
			return y;
		}

		@Override
	    public boolean equals(Object o) {
			if (o == this) return true;
			if (!(o instanceof Position)) {
			    return false;
			}
			Position p = (Position) o;
			return p.getX() == x && p.getY() == y;
	    }
		
		@Override
	    public int hashCode() {
	        return Objects.hash(x, y);
	    }
		
		public String toSring(){
			return String.valueOf(x) + " " + String.valueOf(y);
		}
	}
	
	
}
	












