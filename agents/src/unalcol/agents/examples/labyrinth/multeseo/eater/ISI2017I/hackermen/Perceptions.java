package unalcol.agents.examples.labyrinth.multeseo.eater.ISI2017I.hackermen;

import unalcol.agents.Percept;
import unalcol.agents.simulate.util.SimpleLanguage;

/**
 * Created by larra on 11/03/17.
 */
enum Perceptions {
    WFRONT(0),
    WRIGHT(1),
    WBACK(2),
    WLEFT(3),
    GOAL_REACHED(4),
    FAIL(5),
    AFRONT(6),
    ARIGHT(7),
    ABACK(8),
    ALEFT(9),
    RESOURCE(10),
    RESOURCE_COLOR(11),
    RESOURCE_SHAPE(12),
    RESOURCE_SIZE(13),
    RESOURCE_WEIGHT(14),
    ENERGY(15);

    public static SimpleLanguage language;

    private int perceptionIndex;
    private Perceptions(int _PerceptionIndex){
        perceptionIndex = _PerceptionIndex;
    }

    public Object getPerception(Percept p){
        //System.out.println("perceptindex: " + perceptionIndex + " " + (language.getPercept(perceptionIndex)));

        return p.getAttribute(language.getPercept(perceptionIndex));
    }


    public boolean getBooleanPerception(Percept p){
        //System.out.println("perceptindex: " + perceptionIndex + " " + p.getAttribute(language.getPercept(perceptionIndex)));
        return ( (Boolean) p.getAttribute(language.getPercept(perceptionIndex))).
                booleanValue();
        //return (Boolean)getPerception(p);
    }

    public int getIntPerception(Percept p){
        return (Integer)(getPerception(p));
    }
}
