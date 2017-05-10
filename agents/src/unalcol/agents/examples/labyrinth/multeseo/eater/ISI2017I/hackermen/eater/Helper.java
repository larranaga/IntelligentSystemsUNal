package unalcol.agents.examples.labyrinth.multeseo.eater.ISI2017I.hackermen.eater;

import unalcol.agents.Action;
import unalcol.agents.AgentProgram;
import unalcol.agents.Percept;
import unalcol.agents.examples.labyrinth.LabyrinthUtil;
import unalcol.agents.simulate.util.SimpleLanguage;
import unalcol.types.collection.vector.Vector;

public abstract class Helper implements AgentProgram {
	protected SimpleLanguage language;
	protected Vector<String> cmd = new Vector<String>();

	public Helper() {
	}

	public Helper(SimpleLanguage _language) {
		language = _language;
		Perceptions.language = _language;
		Actions.language = _language;
	}

	public void setLanguage(SimpleLanguage _language) {
		language = _language;
	}

	public void init() {
		cmd.clear();
	}

	public abstract int accion(boolean PF, boolean PD, boolean PA, boolean PI, boolean MT, boolean FAIL, boolean AF,
			boolean AD, boolean AA, boolean AI, boolean RS, int RSID, int EN);

	public Action compute(Percept p) {
		if (cmd.size() == 0) {
			boolean PF = Perceptions.WFRONT.getBooleanPerception(p);
			boolean PD = Perceptions.WRIGHT.getBooleanPerception(p);
			boolean PA = Perceptions.WBACK.getBooleanPerception(p);
			boolean PI = Perceptions.WLEFT.getBooleanPerception(p);
			boolean MT = Perceptions.GOAL_REACHED.getBooleanPerception(p);
			boolean FAIL = Perceptions.FAIL.getBooleanPerception(p);
			boolean AF = Perceptions.AFRONT.getBooleanPerception(p);
			boolean AD = Perceptions.ARIGHT.getBooleanPerception(p);
			boolean AA = Perceptions.ABACK.getBooleanPerception(p);
			boolean AI = Perceptions.ALEFT.getBooleanPerception(p);
			boolean RS = Perceptions.RESOURCE.getBooleanPerception(p);
			int EN = Perceptions.ENERGY.getIntPerception(p);
			int RSID = 0;
			if(RS) {
				//order is color shape size weight
				RSID = 0;
				if(Perceptions.RESOURCE_COLOR.getBooleanPerception(p))
					RSID += (1<<3);
				if(Perceptions.RESOURCE_SHAPE.getBooleanPerception(p))
					RSID += (1<<2);
				if(Perceptions.RESOURCE_SIZE.getBooleanPerception(p))
					RSID += (1<<1);
				if(Perceptions.RESOURCE_WEIGHT.getBooleanPerception(p))
					RSID += (1);
			}
			int d = accion(PF, PD, PA, PI, MT, FAIL, AF, AD, AA, AI, RS, RSID, EN);
			
			if( d == -2 ) cmd.add( LabyrinthUtil.EAT );
			else if (0 <= d && d < 4) {
				for (int i = 1; i <= d; i++) {
					cmd.add(language.getAction(3)); // rotate
				}
				cmd.add(language.getAction(2)); // advance
			} else {
				cmd.add(language.getAction(0)); // die
			}
		}
		String x = cmd.get(0);
		cmd.remove(0);
		return new Action(x);
	}

	public boolean goalAchieved(Percept p) {
		return (((Boolean) p.getAttribute(language.getPercept(4))).booleanValue());
	}
}
