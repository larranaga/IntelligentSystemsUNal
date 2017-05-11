package unalcol.agents.examples.labyrinth.multeseo.eater.ISI2017I.hackermen.eater;

import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import unalcol.agents.examples.labyrinth.multeseo.eater.MultiTeseoEaterLabyrinth;
import unalcol.agents.simulate.util.SimpleLanguage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Stack;

public class MrRobotAgent extends Helper {

	protected   int SIZE = 20;
	private   int ID;
	private   HashMap<Pair, Integer> map;
	private   int x, y, targetX, targetY;
	private   HashSet<Pair> queue;
	private   HashSet<Integer> badFood;
	private   int[] possible;
	private   Pair goal;
	private   Stack<Pair> path;
	private   int[] dx = {-1,0,1,0};
	private   int[] dy = {0,1,0,-1};
	private   boolean[] curp = new boolean[8];
	private   int wait;
	private   int tries;
	private   boolean failed;
	private   boolean removeFather;
	private   HashSet<Pair> visitedDFS;
	private   HashSet<Pair> resources;
	private   boolean eating;
	private   boolean iHaveToEat;
	private   boolean waiting;
	private   int prevEnergy;


	/*
	 * 0 - Casilla sin visitar
	 * 1 - Pared
	 * 2 - No Pared
	 * 3 - Casilla visitada
	 * */

	public MrRobotAgent() {
		ID = 0;
		map = new HashMap<Pair,Integer>(8*SIZE*SIZE);
		x = y = 2*SIZE;
		targetX = -1;
		targetY = -1;
		wait = 0;
		tries = 0;
		failed = removeFather = false;
		queue = new HashSet<Pair>(100);
		possible = new int[4];
		visitedDFS = new HashSet<Pair>(8);
		resources = new HashSet<Pair>(100);
		badFood = new HashSet<>(100);
		eating = false;
		iHaveToEat = false;
		waiting = false;
		prevEnergy = -1;
	}

	public MrRobotAgent(SimpleLanguage _language  ) {
		super(_language);
		ID = 0;
		map = new HashMap<Pair,Integer>(8*SIZE*SIZE);
		x = y = 2*SIZE;
		targetX = -1;
		targetY = -1;
		wait = 0;
		tries = 0;
		failed = removeFather = false;
		queue = new HashSet<Pair>(100);
		possible = new int[4];
		visitedDFS = new HashSet<Pair>(8);
		resources = new HashSet<Pair>(100);
		badFood = new HashSet<>(100);
		eating = false;
		iHaveToEat = false;
		waiting = false;
		prevEnergy = -1;
	}


	public int accion(boolean PF, boolean PD, boolean PA, boolean PI, boolean MT, boolean FAIL, boolean AF, boolean AD, boolean AA, boolean AI, boolean RS, int RSID, int EN) {
		if( MT ) return -1;
		curp[0] = PF; curp[1] = PD; curp[2] = PA; curp[3] = PI;
		curp[4] = AF; curp[5] = AD; curp[6] = AA; curp[7] = AI;
		if(prevEnergy == -1)
		    prevEnergy = EN;
		if(EN <= 0)
            System.out.println("STARVED");

		if(waiting) {
			if(AF) return -1;
			if(PF) {
				waiting = false;
				boolean rmv = resources.remove(new Pair(x,y));
				map.put(new Pair(x+dx[ID], y+dy[ID]), 1);
				Pair food = bfsSearchFood(new Pair(x,y));
				if( food == null ) {
					//Esperar hasta que se mueva
					map.put(new Pair(x+dx[ID], y+dy[ID]), 2);
					waiting = true;
					if(rmv) resources.add(new Pair(x,y));
					return -1;
				}
				makePath(food, null);
				//No hay food cerca a la que alcance a llegar
				if( EN < path.size() ) {
					//Esperar hasta que se mueva
					map.put(new Pair(x+dx[ID], y+dy[ID]), 2);
					goal = null;
					waiting = true;
					if(rmv) resources.add(new Pair(x,y));
					return -1;
				}

				map.put(new Pair(x+dx[ID], y+dy[ID]), 2);
				Pair to = path.pop();
				targetX = to.x; targetY = to.y;
				if(rmv) resources.add(new Pair(x,y));
				return move( x, y, to, ID);
			}
			waiting = false;
			targetX = x + 2*dx[ID];
			targetY = y + 2*dy[ID];
			return 0;
		}

		if(FAIL || failed) {

			//Fallo en 4 casos:
			// 1. Falle cuando estaba explorando - goal == null
			// 2. Falle cuando estaba llendo a un lugar de exploracion pero ya comi - iHaveToEat == true
			// 3. Falle cuando estaba llendo a un lugar de exploracion pero no he comido - iHaveToEat == false
			// 4. Falle cuando estaba llendo a comer porque o si no me muero - el goal esta en resources

			//Solucion:
			// 1. Esperar a ver si se mueve y si no, explorar otra cosa.
			// 2. Esperar a ver si se mueve y si no, explorar otra cosa.
			// 3. Esperar a ver si se mueve y si no, mirar si puedo ir a la segunda comida mas cercana
			// y volver a poner en el path el goal.
			// 4. Esperar a ver si se mueve y si no, mirar si puedo ir a la segunda comida mas cercana

			//Cambiar direccion a la que miramos actualmente
			if( x == targetX) {
				if( y < targetY) ID = 1;
				else if( y > targetY) ID = 3;
			}
			else if( y == targetY) {
				if( x < targetX) ID = 2;
				else if ( x > targetX) ID = 0;
			}

			if( queue.contains( new Pair(x,y) ) ){
				if( !PD && !map.containsKey( new Pair( x + 2*dx[ (ID+1)%4 ] , y + 2*dy[ (ID+1)%4 ] ) ) ){
					targetX = x + 2*dx[ (ID+1)%4 ];
					targetY = y + 2*dy[ (ID+1)%4 ];
					return 1;
				}
				if( !PA && !map.containsKey( new Pair( x + 2*dx[ (ID+2)%4 ] , y + 2*dy[ (ID+2)%4 ] ) ) ){
					targetX = x + 2*dx[ (ID+2)%4 ];
					targetY = y + 2*dy[ (ID+2)%4 ];
					return 2;
				}
				if( !PI && !map.containsKey( new Pair( x + 2*dx[ (ID+3)%4 ] , y + 2*dy[ (ID+3)%4 ] ) ) ){
					targetX = x + 2*dx[ (ID+3)%4 ];
					targetY = y + 2*dy[ (ID+3)%4 ];
					return 3;
				}
			}

			if(wait < 5) {
				wait++;
				failed = true;
				return -1;
			}
			failed = false;
			if( !AF && !PF ) {
				targetX = x + 2*dx[ID];
				targetY = y + 2*dy[ID];
				//System.out.println(x + " " + y + " " + ID +  " WTF");
				return 0;
			}
			
			if(resources.contains(goal)){
				boolean rmv = resources.remove(new Pair(x,y));
				map.put(new Pair(x+dx[ID], y+dy[ID]), 1);
				Pair food = bfsSearchFood(new Pair(x,y));

				if( food == null ) {
					//Esperar hasta que se mueva
					map.put(new Pair(x+dx[ID], y+dy[ID]), 2);
					waiting = true;
					if(rmv) resources.add(new Pair(x,y));
					return -1;

				}
				makePath(food, null);
				//No hay food cerca a la que alcance a llegar
				if( EN < path.size() ) {
					//Esperar hasta que se mueva
					map.put(new Pair(x+dx[ID], y+dy[ID]), 2);
					goal = null;
					waiting = true;
					if(rmv) resources.add(new Pair(x,y));
					return -1;
				}

				map.put(new Pair(x+dx[ID], y+dy[ID]), 2);
				Pair to = path.pop();
				targetX = to.x; targetY = to.y;
				if(rmv) resources.add(new Pair(x,y));
				return move( x, y, to, ID);
			}

			else if(goal == null || !iHaveToEat){
				bfs( new Pair(x+dx[ID], y+dy[ID]) );
				if( path.isEmpty() ) {
					boolean rmv = resources.remove(new Pair(x,y));
					map.put(new Pair(x+dx[ID], y+dy[ID]), 1);
					Pair food = bfsSearchFood(new Pair(x,y));
					if( food == null ) {
						//Esperar hasta que se mueva
						map.put(new Pair(x+dx[ID], y+dy[ID]), 2);
						waiting = true;
						if(rmv) resources.add(new Pair(x,y));
						return -1;

					}
					makePath(food, null);
					//No hay food cerca a la que alcance a llegar
					if( EN < path.size() ) {
						//Esperar hasta que se mueva
						map.put(new Pair(x+dx[ID], y+dy[ID]), 2);
						goal = null;
						waiting = true;
						if(rmv) resources.add(new Pair(x,y));
						return -1;
					}

					map.put(new Pair(x+dx[ID], y+dy[ID]), 2);
					Pair to = path.pop();
					targetX = to.x; targetY = to.y;
					if(rmv) resources.add(new Pair(x,y));
					return move( x, y, to, ID);
				}
				Pair to = path.pop();
				targetX = to.x; targetY = to.y;
				return move( x, y, to, ID);
			}

			else{
				Pair enemy = new Pair(x+dx[ID], y+dy[ID]);
				map.put(enemy, 1);
				Stack<Pair> aux = path;
				Pair go = goal;
				int i = 0;
				while( i < path.size() && !resources.contains(path.get(i)) ) i++;
				if(i == path.size()) {
					waiting = true;
					map.put(enemy, 2);
					return -1;
				}
				resources.remove(path.get(i));
				Pair fl = path.get(i);
				boolean rmv = resources.remove(new Pair(x,y));
				Pair food = bfsSearchFood( new Pair(x,y) );
				makePath(food, null);
				if(EN < path.size()) {
					path = aux;
					goal = go;
					waiting = true;
					map.put(enemy, 2);
					resources.add(fl);
					if(rmv) resources.add(new Pair(x,y));
					return -1;
				}
				if(go.equals(new Pair(x,y))) {
					map.put(enemy, 2);
					resources.add(fl);
					waiting = true;
					if(rmv) resources.add(new Pair(x,y));
					return -1;
				}
				makePath(food,go);
				Pair to = path.pop();
				targetX = to.x; targetY = to.y;
				map.put(enemy, 2);
				resources.add(fl);
				if(rmv) resources.add(new Pair(x,y));
				return move( x, y, to, ID);
			}
		}

		if( removeFather ) queue.remove( new Pair(x, y) );

		removeFather = failed = false;
		tries = wait = 0;

		if( !(targetX == targetY && targetX == -1) ) {
			if( x == targetX) {
				if( y < targetY) ID = 1;
				else if( y > targetY) ID = 3;
			}
			else if( y == targetY) {
				if( x < targetX) ID = 2;
				else if ( x > targetX) ID = 0;
			}
			x = targetX; y = targetY;
			if( RS){
			    if(eating && EN < prevEnergy && !badFood.contains(RSID)){
                    resources.remove(new Pair(x,y));
                    badFood.add(RSID);
                    eating = false;
                    //System.out.println("THIS IS BAD FOOD JUST FOUND");
                }
                else if(!badFood.contains(RSID)){
                    resources.add(new Pair(x, y));
                    if (iHaveToEat || EN < MultiTeseoEaterLabyrinth.MAX_ENERGY_LEVEL / 2) {
                        eating = true;
                        iHaveToEat = false;
                        prevEnergy = EN;
                        return -2;
                    }
                }
			}
		}

		if( eating ){
			if( EN < Math.min(MultiTeseoEaterLabyrinth.MAX_ENERGY_LEVEL, 50) ){
                prevEnergy = EN;
                return -2;
            }
			eating = false;
		}

		if( goal != null ) {
			if( x != goal.x || y != goal.y ) {
				if(path.isEmpty()) bfs(null);
				Pair to = path.pop();
				targetX = to.x;
				targetY = to.y;
				return move( x, y, to, ID);
			}
			queue.remove( goal );
			goal = null;
		}

		map.put(new Pair(x,y), 3);

		int idx = 0;
		int idA = -1;
		for(int i = 0; i < 4; i++) {
			map.put( new Pair( x + dx[ (i+ID)%4 ] , y + dy[ (i+ID)%4 ] ) , curp[i] ? 1:2 );
			Pair aux = new Pair( x + 2*dx[ (i+ID)%4 ], y + 2*dy[ (i+ID)%4 ]);
			if( !curp[i] && ( !map.containsKey( aux ) || map.get(aux) != 3 ) ){
				if( curp[ i+4 ] ) idA = i;
				else possible[ idx++ ] = i;
			}
		}

		if( idA != -1 ) queue.add( new Pair(x, y) );

		int nMove = -1;
		if( idx > 1 ){
			queue.add(new Pair(x,y) );
			nMove = minDepth( idx );
		}


		Pair f = bfsSearchFood( new Pair(x,y) );
		if( f != null ) {
			makePath(f, null);
			goal = null;

			if( EN-5 < path.size() && MultiTeseoEaterLabyrinth.MAX_ENERGY_LEVEL-path.size() > path.size() ) {
				goal = f;
				Pair to = path.pop();
				targetX = to.x;
				targetY = to.y;
				return move( x, y, to, ID);
			}
		}


		if(idx == 0) {
			if( queue.isEmpty() ){

				targetX = x + 2*dx[ (idA+ID)%4 ];
				targetY = y + 2*dy[ (idA+ID)%4 ];

				return idA;
				//FALTA: Cambio si acaso no queremos tener un posible fallo y preferimos esperar
			}
			bfs(null);
			if( idA != -1 ) queue.add( new Pair(x, y) );
			if(x == goal.x && y == goal.y) {
				goal = null;
				bfs(new Pair(x+dx[idA], y+dy[idA]));
				if( path.isEmpty() ) {
					boolean rmv = resources.remove(new Pair(x,y)); 
					
					map.put(new Pair(x+dx[ID], y+dy[ID]), 1);
					Pair food = bfsSearchFood(new Pair(x,y));
					if( food == null ) {
						//Esperar hasta que se mueva
						map.put(new Pair(x+dx[ID], y+dy[ID]), 2);
						waiting = true;
						if(rmv) resources.add(new Pair(x,y));
						return -1;

					}
					makePath(food, null);

					if( EN < path.size() ) {
						//Esperar hasta que se mueva
						map.put(new Pair(x+dx[ID], y+dy[ID]), 2);
						goal = null;
						waiting = true;
						if(rmv) resources.add(new Pair(x,y));
						return -1;
					}

					map.put(new Pair(x+dx[ID], y+dy[ID]), 2);
					Pair to = path.pop();
					targetX = to.x; targetY = to.y;
					if(rmv) resources.add(new Pair(x,y));
					return move( x, y, to, ID);
				}
				else {
					Pair to = path.pop();
					targetX = to.x;
					targetY = to.y;
					return move( x, y, to, ID);
				}
			}
			//FALTA: Revisar Margen de error
			//Esto significa que yo puedo explorar 13 posiciones mas despues de llegar
			//a mi objetivo
			if( EN-13 > path.size()) {
				Pair food = bfsSearchFood(goal);
				if(food != null) {
					makePath(food, goal);
					Stack<Pair> aux = path;
					Pair food2 = bfsSearchFood( new Pair(x,y) ); 
					makePath(food2, goal);
					if(aux.size() < path.size()) path = aux;
					iHaveToEat = true;
				}
			}
			Pair to = path.pop();
			targetX = to.x; targetY = to.y;
			return move( x, y, to, ID);
		}


		if( nMove == -1 ) nMove = (int)(Math.random()*idx);

		targetX = x + 2*dx[ (possible[nMove]+ID)%4 ];
		targetY = y + 2*dy[ (possible[nMove]+ID)%4 ];

		return possible[nMove];
	}

	private Pair bfsSearchFood( Pair from ) {

		if(resources.isEmpty()) return null;

		LinkedList<Node> queue = new LinkedList<Node>();
		Node initial = new Node( from, new Pair(-1,-1) ); 
		queue.add( initial );
		Node cur = initial;
		HashMap<Pair, Pair> visited = new HashMap<Pair, Pair>();

		while( !resources.contains(cur.c) && !queue.isEmpty() ) {
			cur = queue.removeFirst();
			if( visited.containsKey(cur.c) ) continue;
			visited.put(cur.c, cur.f);
			for(int j = 0; j < 4; j++) {
				int ix = cur.c.x + dx[j];
				int iy = cur.c.y + dy[j];
				Pair iPos = new Pair(ix,iy);
				if(map.containsKey(iPos) && map.get(iPos) == 2) 
					queue.add( new Node ( new Pair( ix+dx[j] , iy+dy[j] ), cur.c ) );
			}
		}
		return cur.c;
	}

	//Realiza el path desde donde estoy a las dos posiciones que le paso
	//Guarda en goal la posicion mas lejana a la que queremos llegar
	private int makePath( Pair goal1, Pair goal2 ) {
		if(goal2 == null) {
			LinkedList<Node> queue = new LinkedList<Node>();
			Node initial = new Node( new Pair(x,y), new Pair(-1,-1) ); 
			queue.add( initial );
			Node cur = initial;
			HashMap<Pair, Pair> visited = new HashMap<Pair, Pair>();

			while( !goal1.equals(cur.c) && !queue.isEmpty() ) {
				cur = queue.removeFirst();
				if( visited.containsKey(cur.c) ) continue;
				visited.put(cur.c, cur.f);
				for(int j = 0; j < 4; j++) {
					int ix = cur.c.x + dx[j];
					int iy = cur.c.y + dy[j];
					Pair iPos = new Pair(ix,iy);
					if(map.containsKey(iPos) && map.get(iPos) == 2) 
						queue.add( new Node ( new Pair( ix+dx[j] , iy+dy[j] ), cur.c ) );
				}
			}
			path = new Stack<Pair>();

			goal = cur.c;
			Pair bt = goal;
			while( !bt.equals(initial.c) ){
				path.push(bt);
				bt = visited.get(bt);
			}
			return path.size();
		}
		else {
			//Camino desde goal1 hasta goal2
			LinkedList<Node> queue = new LinkedList<Node>();
			Node initial = new Node( goal1, new Pair(-1,-1) ); 
			queue.add( initial );
			Node cur = initial;
			HashMap<Pair, Pair> visited = new HashMap<Pair, Pair>();

			while( !goal2.equals(cur.c) && !queue.isEmpty() ) {
				cur = queue.removeFirst();
				if( visited.containsKey(cur.c) ) continue;
				visited.put(cur.c, cur.f);
				for(int j = 0; j < 4; j++) {
					int ix = cur.c.x + dx[j];
					int iy = cur.c.y + dy[j];
					Pair iPos = new Pair(ix,iy);
					if(map.containsKey(iPos) && map.get(iPos) == 2) 
						queue.add( new Node ( new Pair( ix+dx[j] , iy+dy[j] ), cur.c ) );
				}
			}
			path = new Stack<Pair>();

			goal = cur.c;
			Pair bt = goal;
			while( !bt.equals(goal1) ){
				path.push(bt);
				bt = visited.get(bt);
			}

			int sz = path.size();

			//Camino desde actual hasta goal1
			queue = new LinkedList<Node>();
			initial = new Node( new Pair(x,y), new Pair(-1,-1) ); 
			queue.add( initial );
			cur = initial;
			visited = new HashMap<Pair, Pair>();

			while( !goal1.equals(cur.c) && !queue.isEmpty() ) {
				cur = queue.removeFirst();
				if( visited.containsKey(cur.c) ) continue;
				visited.put(cur.c, cur.f);
				for(int j = 0; j < 4; j++) {
					int ix = cur.c.x + dx[j];
					int iy = cur.c.y + dy[j];
					Pair iPos = new Pair(ix,iy);
					if(map.containsKey(iPos) && map.get(iPos) == 2) 
						queue.add( new Node ( new Pair( ix+dx[j] , iy+dy[j] ), cur.c ) );
				}
			}
			Pair g = cur.c;
			bt = g;
			while( !bt.equals(initial.c) ){
				path.push(bt);
				bt = visited.get(bt);
			}
			return path.size() - sz;
		}
	}

	private int minDepth( int idx ){
		for( int i = 0; i < idx; i++ ){
			visitedDFS.clear();
			int posX = x + 2*dx[ (possible[i]+ID)%4 ];
			int posY = y + 2*dy[ (possible[i]+ID)%4 ];
			if( DFS( posX, posY, 1, 9 ) ) return i;
		}
		return -1;
	}

	private boolean DFS( int cX, int cY, int curD, int limitD ){
		if( curD > limitD )return false;
		visitedDFS.add( new Pair(cX, cY) );
		for( int i = 0; i < 4; i++ ){
			if( !map.containsKey(new Pair( cX + dx[i] , cY + dy[i] ) ) && !visitedDFS.contains( new Pair(cX + 2*dx[i], cY + 2*dy[i]) ) ){
				if( cX + 2*dx[i] >= 0 && cX + 2*dx[i] < 4*SIZE && cY + 2*dy[i] >= 0 && cY + 2*dy[i] < 4*SIZE ){
					if( !DFS( cX + 2*dx[i], cY + 2*dy[i], curD + 1, limitD ) ) return false;
				}
			}
		}
		return true;
	}

	private int move(int x, int y, Pair b, int ID) {
		if(x == b.x) {
			if( y < b.y ) return (5-ID) % 4;
			else return 3-ID;
		}
		else {
			if( x < b.x ) return (6-ID) % 4;
			else return (4-ID) % 4;
		}
	}

	private void bfs(Pair enemy) {
		Pair enemyPos = null;
		if(enemy != null) {
			enemyPos = new Pair( (x+enemy.x)/2 , (y+enemy.y)/2); 
			map.put(enemyPos, 1);
		}

		LinkedList<Node> queue = new LinkedList<Node>();
		Node initial = new Node( new Pair(x,y), new Pair(-1,-1) ); 
		queue.add( initial );
		Node current = initial;
		HashMap<Pair, Pair> visited = new HashMap<Pair, Pair>();

		while( !this.queue.contains(current.c) && !queue.isEmpty() ) {
			current = queue.removeFirst();
			if( visited.containsKey(current.c) ) continue;
			visited.put(current.c, current.f);
			for(int j = 0; j < 4; j++) {
				int nextX = current.c.x + dx[j];
				int nextY = current.c.y + dy[j];
				Pair iPos = new Pair(nextX,nextY);
				if(map.containsKey(iPos) && map.get(iPos) == 2) 
					queue.add( new Node ( new Pair( nextX+dx[j] , nextY+dy[j] ), current.c ) );
			}
		}
		path = new Stack<Pair>();
		if(enemy != null) map.put(enemyPos, 2);

		if( !this.queue.contains(current.c) ) return;

		goal = current.c;
		Pair bt = goal;
		while( !bt.equals(initial.c) ){
			path.push(bt);
			bt = visited.get(bt);
		}

	}

	protected   class Pair{
		int x; 
		int y;

		Pair(int x, int y) {
			this.x = x;
			this.y = y;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + x;
			result = prime * result + y;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Pair other = (Pair) obj;
			if (x != other.x)
				return false;
			if (y != other.y)
				return false;
			return true;
		}

		public String toString(){
			return "( " + x + ", " + y + " )"; 
		}

	}

	protected   class Node {
		Pair c;
		Pair f;

		public Node( Pair c, Pair f) {
			this.c = c;
			this.f = f;
		}
	}
}
