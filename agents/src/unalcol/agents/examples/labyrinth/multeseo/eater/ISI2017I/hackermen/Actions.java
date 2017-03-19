package unalcol.agents.examples.labyrinth.multeseo.eater.ISI2017I.hackermen;

import unalcol.agents.Action;
import unalcol.agents.simulate.util.SimpleLanguage;

/**
 * Created by larra on 10/03/2017.
 */
enum Actions{
    NOP(0),
    DIE(1),
    ADVANCE(2),
    ROTATE(3),
    EAT(4);

    public static SimpleLanguage language;
    private int actionIndex;
    private Actions(int _actionIndex){
        actionIndex = _actionIndex;
    }

    public Action getAction(){
        return new Action(language.getAction(actionIndex));
    }

    public static boolean compare(Action action1, Action action2){
        return action1.getCode().equals(action2.getCode());
    }
}
