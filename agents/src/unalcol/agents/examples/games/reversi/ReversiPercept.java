/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package unalcol.agents.examples.games.reversi;

import unalcol.agents.Percept;

/**
 *
 * @author Jonatan
 */
public class ReversiPercept extends Percept{
    
    protected Board board;
    protected Clock clock;
  
    public ReversiPercept(Board board, Clock clock ) {
      this.board = board;
      this.clock = clock;
    }
    
    @Override
    public Object getAttribute( String code ){
        if( code.equals(unalcol.agents.examples.games.reversi.Reversi.TURN) ){
            if( clock.white_turn() ){
                return unalcol.agents.examples.games.reversi.Reversi.WHITE;
            }else{
                return unalcol.agents.examples.games.reversi.Reversi.BLACK;
            }
        }else{
            if( code.equals(unalcol.agents.examples.games.reversi.Reversi.WHITE + "_" + unalcol.agents.examples.games.reversi.Reversi.TIME) ){
                    return clock.white_time_string();
            }else{
                if( code.equals(unalcol.agents.examples.games.reversi.Reversi.BLACK + "_" + unalcol.agents.examples.games.reversi.Reversi.TIME) ){
                        return clock.white_time_string();
                }else{
                    if( code.equals(unalcol.agents.examples.games.reversi.Reversi.SIZE ) ){
                        return ""+board.values.length;
                    }else{
                        String[] v = code.split(":");
                        int i = Integer.parseInt(v[0]);
                        int j = Integer.parseInt(v[1]);
                        switch( board.values[i][j]){
                            case -1:
                                return unalcol.agents.examples.games.reversi.Reversi.BLACK;
                            case 1:
                                return unalcol.agents.examples.games.reversi.Reversi.WHITE;
                            default:
                                return unalcol.agents.examples.games.reversi.Reversi.SPACE;
                        }
                    }
                }    
            }            
        }
    }
}
