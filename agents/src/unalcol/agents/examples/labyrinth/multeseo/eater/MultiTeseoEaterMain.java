package unalcol.agents.examples.labyrinth.multeseo.eater;

import unalcol.agents.Agent;
import unalcol.agents.AgentProgram;
import unalcol.agents.examples.labyrinth.Labyrinth;
import unalcol.agents.examples.labyrinth.LabyrinthDrawer;
import unalcol.agents.examples.labyrinth.multeseo.eater.ISI2017I.ARRE.ARREA2;
import unalcol.agents.examples.labyrinth.multeseo.eater.ISI2017I.CEM.TeseoCEM;
import unalcol.agents.examples.labyrinth.multeseo.eater.ISI2017I.parcelona.MessiTeseo;
import unalcol.agents.examples.labyrinth.multeseo.eater.ISI2017I.Mirmidones.SuperTeseo;
import unalcol.agents.examples.labyrinth.multeseo.eater.ISI2017I.SCS.SimpleAgentSCS;
import unalcol.agents.examples.labyrinth.multeseo.eater.ISI2017I.hackermen.HackermenAgentProgram;
import unalcol.agents.examples.labyrinth.multeseo.eater.ISI2017I.thewise.Agent1;
import unalcol.agents.simulate.util.SimpleLanguage;
import unalcol.types.collection.vector.Vector;

public class MultiTeseoEaterMain {
    private static SimpleLanguage getLanguage(){
	    return  new SimpleLanguage( new String[]{"front", "right", "back", "left", "treasure", "fail",
	        "afront", "aright", "aback", "aleft", "resource", "resource-color", "resource-shape", "resource-size", "resource-weight"},
	                                   new String[]{"no_op", "die", "advance", "rotate", "eat"}
	                                   );
	  }

	  public static void main( String[] argv ){
	     AgentProgram[] teseo = new AgentProgram[12];

	    teseo[1] = new Agent1( getLanguage() );
	    teseo[2] = new TeseoCEM( getLanguage() );
	    teseo[3] = new HackermenAgentProgram(getLanguage());
	    teseo[4] = new SuperTeseo(getLanguage());
		teseo[6] = new ARREA2(getLanguage());
		teseo[7] = new MessiTeseo(getLanguage());
		teseo[8] = new SimpleAgentSCS(getLanguage());

	    
	    LabyrinthDrawer.DRAW_AREA_SIZE = 600;
	    LabyrinthDrawer.CELL_SIZE = 40;
	    Labyrinth.DEFAULT_SIZE = 15;
	    
	    Agent agent1 = new Agent(teseo[7]);
	    Agent agent2 = new Agent(teseo[3]);
	    
	    //Agent agent3 = new Agent(p3);
	    Vector<Agent> agent = new Vector<Agent>();
	    agent.add(agent1);
	    agent.add(agent2);
	    //Agent agent = new Agent( new RandomReflexTeseoAgentProgram( getLanguage() ) );
	    MultiTeseoEaterMainFrame frame = new MultiTeseoEaterMainFrame( agent, getLanguage() );
	    frame.setVisible(true); 
	  }
}
