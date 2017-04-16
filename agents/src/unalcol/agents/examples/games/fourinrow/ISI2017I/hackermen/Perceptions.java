package unalcol.agents.examples.games.fourinrow.ISI2017I.hackermen;

import unalcol.agents.Percept;
import unalcol.agents.examples.games.fourinrow.FourInRow;
import unalcol.agents.simulate.util.SimpleLanguage;

/**
 * Created by larra on 12/04/2017.
 */
public enum Perceptions {
    TURN(FourInRow.TURN),
    SPACE(FourInRow.SPACE),
    W_TIME(FourInRow.WHITE + "_" + FourInRow.TIME),
    B_TIME(FourInRow.BLACK + "_" + FourInRow.TIME),
    SIZE(FourInRow.SIZE),
    POSITION("");

    public static SimpleLanguage language;
    private String query;
    private Perceptions(String _query){
        query = _query;
    }

    public Object getPerception(Percept p){
        return p.getAttribute(query);
    }

    public boolean getBooleanPerception(Percept p){
        return ((Boolean) getPerception(p)).booleanValue();
    }

    public String getStringPerception(Percept p){
        return (String) getPerception(p);
    }
    public int getIntPerception(Percept p){
        return Integer.valueOf(getStringPerception(p));
    }

    public String getPositionPerception(Percept p, int x, int y){
        String player = (String) p.getAttribute(x + ":" + y );
        return player;
    }
}
