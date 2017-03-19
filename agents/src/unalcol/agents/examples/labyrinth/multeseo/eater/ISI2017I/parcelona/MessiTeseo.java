/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package unalcol.agents.examples.labyrinth.multeseo.eater.ISI2017I.parcelona;

/*
percepciones ="front", "right", "back", "left", "treasure", "fail","afront",
    "aright", "aback", "aleft", "resource", "resource-color", "resource-shape", "resource-size", "resource-weight"
acciones = "no_op", "die", "advance", "rotate", "eat"
 */
import unalcol.agents.Action;
import unalcol.agents.Percept;
import unalcol.agents.simulate.util.SimpleLanguage;
import unalcol.agents.examples.labyrinth.teseo.simple.*;
/**
 *
 * @author Jose Miguel Carvajal, Raul Ricardo Orcasitas, Christian Camilo Vaca
 */
public class MessiTeseo extends SimpleTeseoAgentProgram {

    private String previous;
    public MessiTeseo() {}

    public MessiTeseo(SimpleLanguage _language  ) {
        super(_language);
        previous = "";
    }


    @Override
    public int accion(boolean PF, boolean PD, boolean PA, boolean PI, boolean MT, boolean FAIL) {
        //System.out.println( FAIL );
        if (MT) return -1;
        boolean flag = true;
        int k=0;
        if (PF){
            k = 1;
        }
        if (!PF && PI && PD)
            k = 0;
        if (!PF && !PD && !previous.equals("rotate")){
            k = 1;
        }
        if (!PF && !PI && previous.equals("rotate")){
            k = 3;
        }
        if (PF && !PI){
            k = 3;
        }
        if (PF && !PI && !PD)
            k = 1;
        return k;
    }



    @Override
    public Action compute(Percept p){
        if( cmd.size() == 0 ){

            boolean PF = ( (Boolean) p.getAttribute(language.getPercept(0))).
                    booleanValue();
            boolean PD = ( (Boolean) p.getAttribute(language.getPercept(1))).
                    booleanValue();
            boolean PA = ( (Boolean) p.getAttribute(language.getPercept(2))).
                    booleanValue();
            boolean PI = ( (Boolean) p.getAttribute(language.getPercept(3))).
                    booleanValue();
            boolean MT = ( (Boolean) p.getAttribute(language.getPercept(4))).
                    booleanValue();
            boolean FAIL = ( (Boolean) p.getAttribute(language.getPercept(5))).
                    booleanValue();
            boolean AF = ( (Boolean) p.getAttribute(language.getPercept(6))).
                    booleanValue();
            boolean AD = ( (Boolean) p.getAttribute(language.getPercept(7))).
                    booleanValue();
            boolean AA = ( (Boolean) p.getAttribute(language.getPercept(8))).
                    booleanValue();
            boolean AI = ( (Boolean) p.getAttribute(language.getPercept(9))).
                    booleanValue();

            int d = accion(PF, PD, PA, PI, MT, FAIL);
            if (0 <= d && d < 4) {
                for (int i = 1; i <= d; i++) {
                    cmd.add(language.getAction(3)); //rotate
                }
                cmd.add(language.getAction(2)); // advance
            }
            else {
                cmd.add(language.getAction(0)); // die
            }
        }
        String x = cmd.get(0);
        System.out.println(x);
        cmd.remove(0);
        previous = x;
        return new Action(x);
    }


}
