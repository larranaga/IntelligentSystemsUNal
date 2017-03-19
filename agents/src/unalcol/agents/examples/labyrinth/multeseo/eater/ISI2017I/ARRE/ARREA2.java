package unalcol.agents.examples.labyrinth.multeseo.eater.ISI2017I.ARRE;

import unalcol.agents.Action;
import unalcol.agents.AgentProgram;
import unalcol.agents.Percept;
import unalcol.agents.simulate.util.SimpleLanguage;
import unalcol.types.collection.vector.Vector;
import java.util.Hashtable;

/**
 * Created by rodesv on 12/03/17.
 */
public class ARREA2 implements AgentProgram {

    protected SimpleLanguage sl;
    protected Vector<String> cmd = new Vector<>();
    protected Percept previousState;
    protected Integer x = 0;
    protected Integer y = 0;
    protected boolean win = false;
    protected Integer position = 0;
    protected Hashtable<String, Integer> hs = new Hashtable<>();

    public ARREA2(SimpleLanguage sl){
        this.sl = sl;
        this.previousState = null;
    }
    private void Front_move (){
        if(position==0){
            cmd.add(sl.getAction(2));
        }else if(position ==1){
            cmd.add(sl.getAction(3));
            cmd.add(sl.getAction(3));
            cmd.add(sl.getAction(3));
            cmd.add(sl.getAction(2));
        }else if(position == 2){
            cmd.add(sl.getAction(3));
            cmd.add(sl.getAction(3));
            cmd.add(sl.getAction(2));
        }else{
            cmd.add(sl.getAction(3));
            cmd.add(sl.getAction(2));
        }
        position = 0;
    }
    private void Back_move (){
        if(position==2){
            cmd.add(sl.getAction(2));
        }else if(position == 3){
            cmd.add(sl.getAction(3));
            cmd.add(sl.getAction(3));
            cmd.add(sl.getAction(3));
            cmd.add(sl.getAction(2));
        }else if(position == 0){
            cmd.add(sl.getAction(3));
            cmd.add(sl.getAction(3));
            cmd.add(sl.getAction(2));
        }else{
            cmd.add(sl.getAction(3));
            cmd.add(sl.getAction(2));
        }
        position = 2;
    }
    private void Right_move (){
        if(position==1){
            cmd.add(sl.getAction(2));
        }else if(position ==2){
            cmd.add(sl.getAction(3));
            cmd.add(sl.getAction(3));
            cmd.add(sl.getAction(3));
            cmd.add(sl.getAction(2));
        }else if(position == 3){
            cmd.add(sl.getAction(3));
            cmd.add(sl.getAction(3));
            cmd.add(sl.getAction(2));
        }else{
            cmd.add(sl.getAction(3));
            cmd.add(sl.getAction(2));
        }
        position = 1;
    }
    private void Left_move (){
        if(position==3){
            cmd.add(sl.getAction(2));
        }else if(position == 0){
            cmd.add(sl.getAction(3));
            cmd.add(sl.getAction(3));
            cmd.add(sl.getAction(3));
            cmd.add(sl.getAction(2));
        }else if(position == 1){
            cmd.add(sl.getAction(3));
            cmd.add(sl.getAction(3));
            cmd.add(sl.getAction(2));
        }else{
            cmd.add(sl.getAction(3));
            cmd.add(sl.getAction(2));
        }
        position = 3;
    }

    @Override
    public Action compute(Percept p) {
        if (this.cmd.size() == 0)
        {
            if(!hs.containsKey(x+ " "+(y+1)))
                hs.put(x+ " "+(y+1), 0);
            if(!hs.containsKey(x+ " "+(y-1)))
                hs.put(x+ " "+(y-1), 0);
            if(!hs.containsKey((x+1)+ " "+y))
                hs.put((x+1)+ " "+y, 0);
            if(!hs.containsKey((x-1)+ " "+y))
                hs.put((x-1)+ " "+y, 0);
            boolean PF = hs.get(x+ " "+(y+1)) == -1;
            boolean PD = hs.get((x+1)+ " "+y) == -1;
            boolean PA = hs.get(x+ " "+(y-1)) == -1;
            boolean PI = hs.get((x-1)+ " "+y) == -1;

            if(position == 0){
                PF = PF || (Boolean) p.getAttribute(sl.getPercept(0));
                PD = PD || (Boolean) p.getAttribute(sl.getPercept(1));
                PA = PA || (Boolean) p.getAttribute(sl.getPercept(2));
                PI = PI || (Boolean) p.getAttribute(sl.getPercept(3));
            } else if(position == 1){
                PF = PF || (Boolean) p.getAttribute(sl.getPercept(3));
                PD = PD || (Boolean) p.getAttribute(sl.getPercept(0));
                PA = PA || (Boolean) p.getAttribute(sl.getPercept(1));
                PI = PI || (Boolean) p.getAttribute(sl.getPercept(2));
            } else if(position == 2){
                PF = PF || (Boolean) p.getAttribute(sl.getPercept(2));
                PD = PD || (Boolean) p.getAttribute(sl.getPercept(3));
                PA = PA || (Boolean) p.getAttribute(sl.getPercept(0));
                PI = PI || (Boolean) p.getAttribute(sl.getPercept(1));
            } else {
                PF = PF || (Boolean) p.getAttribute(sl.getPercept(1));
                PD = PD || (Boolean) p.getAttribute(sl.getPercept(2));
                PA = PA || (Boolean) p.getAttribute(sl.getPercept(3));
                PI = PI || (Boolean) p.getAttribute(sl.getPercept(0));
            }
            boolean MT = (Boolean) p.getAttribute(sl.getPercept(4));

            //System.out.println(hs.get(x+ " "+(y))+" "+hs.get(x+ " "+(y+1))+" "+hs.get((x+1)+ " "+y)+" "+hs.get(x+ " "+(y-1))+" "+hs.get((x-1)+" "+y));
            //System.out.println(x+" "+y);
            if (goalTest(p))
                cmd.add(sl.getAction(1));

            if (MT)
                cmd.add(sl.getAction(1));

            int minpf = Integer.MAX_VALUE, blo = 0, nx = x, ny = y;
            if(!PF ){
                if(hs.get(x+ " "+(y+1))<minpf){
                    minpf = hs.get(x+ " "+(y+1));
                    nx = x;
                    ny = y+1;
                }
                blo++;
            }
            if(!PA ){
                if(hs.get(x+ " "+(y-1))<minpf){
                    minpf = hs.get(x+ " "+(y-1));
                    nx = x;
                    ny = y-1;
                }
                blo++;
            }
            if(!PD ){
                if(hs.get((x+1)+ " "+y)<minpf){
                    minpf = hs.get((x+1)+ " "+y);
                    nx = x+1;
                    ny = y;
                }
                blo++;
            }
            if(!PI ){
                if(hs.get((x-1)+ " "+y)<minpf){
                    minpf = hs.get((x-1)+ " "+y);
                    nx = x-1;
                    ny = y;
                }
                blo++;
            }
            if(ny > y){
                Front_move();
                //System.out.println("Arriba");
            }else if(ny < y){
                Back_move();
                //System.out.println("Abajo");
            }else if(nx > x){
                Right_move();
                //System.out.println("Derecha");
            }else{
                Left_move();
                //System.out.println("Izquierda");
            }
            int a = 1;
            if(hs.containsKey(x+" "+y))
                a += hs.get(x+" "+y);
                hs.remove(x+" "+y);
            hs.put(x+" "+y, a);
            if(blo==1){
                if(hs.containsKey(x+" "+y))
                    hs.remove(x+" "+y);
                hs.put(x+" "+y, -1);
            }
            x = nx;
            y = ny;

        }
        String action = cmd.get(0);
        cmd.remove(0);
        return new Action(action);
    }

    public Boolean goalTest(Percept p){
        return (Boolean)p.getAttribute(this.sl.getPercept(4));
    }

    @Override
    public void init() {
        this.cmd.clear();
    }
}
