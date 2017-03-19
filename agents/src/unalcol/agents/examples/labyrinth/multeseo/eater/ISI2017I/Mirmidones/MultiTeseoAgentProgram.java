package unalcol.agents.examples.labyrinth.multeseo.eater.ISI2017I.Mirmidones;

import unalcol.agents.AgentProgram;
import unalcol.agents.Percept;
import unalcol.agents.simulate.util.SimpleLanguage;
import unalcol.types.collection.vector.*;
import unalcol.agents.Action;

public abstract class MultiTeseoAgentProgram implements AgentProgram {

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2017</p>
 *
 * <p>Company: Universidad Nacional de Colombia</p>
 *
 * @author Luis Ernesto Gil / Juan Camilo Rubio
 * @version 1.0
 */
  protected SimpleLanguage language;
  protected Vector<String> cmd = new Vector<String>();
  public MultiTeseoAgentProgram( ) {
  }

  public MultiTeseoAgentProgram(   SimpleLanguage _language  ) {
	  language = _language;
  }

  public void setLanguage(  SimpleLanguage _language ){
    language = _language;
  }

  public void init(){
    cmd.clear();
  }

  public abstract int accion( boolean PF, //Pared al frente 
		  						boolean PD, //Pared a la derecha
		  						boolean PA, //Pared atras
		  						boolean PI, //Pared a la izquierda
		  						boolean MT, //Treasure, llega a la meta
		  						boolean FAIL, //falla
		  						boolean AF, //Agente al frente
		  						boolean AD, //Agente a la derecha
		  						boolean AA, //Agente atras
		  						boolean AI //Agente a la izquierda
		  						);

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
      
      int d = accion(PF, PD, PA, PI, MT, FAIL, AF, AD, AA, AI);
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
    cmd.remove(0);
    return new Action(x);
  }

  public boolean goalAchieved( Percept p ){;
    return (((Boolean)p.getAttribute(language.getPercept(4))).booleanValue());
  }
  
}