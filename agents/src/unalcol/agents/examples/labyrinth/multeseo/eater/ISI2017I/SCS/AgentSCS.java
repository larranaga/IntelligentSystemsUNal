package unalcol.agents.examples.labyrinth.multeseo.eater.ISI2017I.SCS;

import unalcol.agents.Action;
import unalcol.agents.AgentProgram;
import unalcol.agents.Percept;
import unalcol.agents.simulate.util.SimpleLanguage;
import unalcol.types.collection.vector.Vector;

public abstract class AgentSCS implements AgentProgram {

	protected SimpleLanguage language;
	protected Vector<String> cmd = new Vector<String>();
	
	//Constructors
	public AgentSCS( ) {
	}

	public AgentSCS(   SimpleLanguage _language  ) {
		language = _language;
	}
	
	//Agent Language
	public void setLanguage(SimpleLanguage _language) {
		language = _language;
	}
	
	//Initialize
	public void init() {
		cmd.clear();
	}
	
	//Compute perceptions
	public Action compute(Percept p) {
		if (cmd.size() == 0) {
			
			//Walls, Finish, Die
			boolean PF = ((Boolean) p.getAttribute(language.getPercept(0))).booleanValue();
			boolean PD = ((Boolean) p.getAttribute(language.getPercept(1))).booleanValue();
			boolean PA = ((Boolean) p.getAttribute(language.getPercept(2))).booleanValue();
			boolean PI = ((Boolean) p.getAttribute(language.getPercept(3))).booleanValue();
			boolean MT = ((Boolean) p.getAttribute(language.getPercept(4))).booleanValue();
			boolean FAIL = ((Boolean) p.getAttribute(language.getPercept(5))).booleanValue();
			
			/*
			//Agents
			boolean AF = ((Boolean) p.getAttribute(language.getPercept(6))).booleanValue();
			boolean AD = ((Boolean) p.getAttribute(language.getPercept(7))).booleanValue();
			boolean AA = ((Boolean) p.getAttribute(language.getPercept(8))).booleanValue();
			boolean AI = ((Boolean) p.getAttribute(language.getPercept(9))).booleanValue();
			
			//Resources
			boolean RE = ((Boolean) p.getAttribute(language.getPercept(10))).booleanValue();
			boolean RC = ((Boolean) p.getAttribute(language.getPercept(11))).booleanValue();
			boolean RSh = ((Boolean) p.getAttribute(language.getPercept(12))).booleanValue();
			boolean RS = ((Boolean) p.getAttribute(language.getPercept(13))).booleanValue();
			boolean RW = ((Boolean) p.getAttribute(language.getPercept(14))).booleanValue();
			*/
			//int d = accion(PF, PD, PA, PI, MT, FAIL, AF, AD, AA, AI, RE, RC, RSh, RS, RW);
			int d = accion(PF, PD, PA, PI, MT, FAIL);
			if (0 <= d && d < 4) {
				for (int i = 1; i <= d; i++) {
					cmd.add(language.getAction(3)); // rotate
//					redPasos();
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
	
//	public abstract void redPasos();
	
	//Action method 
	public abstract int accion(
			boolean PF, boolean PD, boolean PA, boolean PI, 			//Moves
			boolean MT, boolean FAIL									//Finish, Die
			//boolean AF, boolean AD, boolean AA, boolean AI,				//Agents
			//boolean RE, boolean RC, boolean RSh,boolean RS,boolean RW	//Resources
			);
	
	//Find the exit
	public boolean goalAchieved(Percept p) {
		return (((Boolean) p.getAttribute(language.getPercept(4))).booleanValue());
	}

}
