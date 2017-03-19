package unalcol.agents.examples.labyrinth.multeseo.eater.ISI2017I.Mirmidones;
import java.util.HashMap;

import unalcol.agents.simulate.util.SimpleLanguage;
import unalcol.types.collection.list.Stack;


public class SuperTeseoOld extends MultiTeseoAgentProgram{
	int MOVES[][] = new int[][]{{1, 0}, {0, 1}, {-1, 0}, {0, -1}};
	
	HashMap<String, Integer> mem;
	Stack<Integer> back;
	String now;
	int head;
	int bad;

	
	public SuperTeseoOld(){ }
	
	public SuperTeseoOld(   SimpleLanguage _language  ) {
	  	  super(_language);
	  	  initMem();
    }
	
	public void initMem( ){
		mem = new HashMap<String, Integer>();
		back = new Stack<>();
		now = "0;0";
		head = 0;
		bad = 0;
	}
	
	public String makeMove( String position, int direction ){
		String[] newPosition = position.split(";");
		return String.valueOf(Integer.parseInt(newPosition[0]) + MOVES[direction][0]) + ";" + String.valueOf(Integer.parseInt(newPosition[1]) + MOVES[direction][1]);
	}
	
	public int inverseMove( int direction ){
		return (direction + 2) % 4;
	}
	
	
	@Override
	public int accion(boolean PF, boolean PD, boolean PA, boolean PI, boolean MT, boolean FAIL, 
			boolean AF, boolean AD, boolean AA, boolean AI) {
    	System.out.println( FAIL );
    	
        if (MT) return -1;
        boolean flag = true;
        int k=-1;
        Boolean[] walls = new Boolean[]{PF, PD, PA, PI};
        
        

        if( !mem.containsKey(now) )
        	mem.put(now, 0);
        else
        	bad++;
        
        System.out.println(bad);
        
        while( flag ){
            for (int i = 0; i < walls.length; i++) {
            	int headPerhaps = (head + i) % 4;
            	String nowPerhaps  = makeMove(now, headPerhaps);
            	
            	if( !walls[i] && !mem.containsKey(nowPerhaps)){
            		now = nowPerhaps;
            		head = headPerhaps;
            		back.push(inverseMove(head));
					k = i;
					flag = walls[i];
					break;
            	}
        	}
            if(k == -1){
            	int backHead = back.pop();
            	int turns = (backHead - head) % 4;
            	if(turns < 0) turns += 4;
            	k = turns;
            	now = makeMove(now, backHead);
            	head = backHead;
            	flag = walls[head];
            	break;
            }
            
            
        }
        /*try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
        return k;
    }
	
	
	
}