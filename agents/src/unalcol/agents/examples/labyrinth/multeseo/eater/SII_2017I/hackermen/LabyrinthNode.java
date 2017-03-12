package unalcol.agents.examples.labyrinth.multeseo.eater.SII_2017I.hackermen;

/**
 * Created by larra on 11/03/17.
 */
public class LabyrinthNode {

    private int x, y;

    public LabyrinthNode(){
        x = y = 0;
    }

    public LabyrinthNode(int _x, int _y){
        x = _x;
        y = _y;
    }

    public int getX(){return x;}

    public int getY(){return y;}

    @Override
    public int hashCode() {
        final int width = 100;
        return x +(y*width);
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj)
            return true;
        if( obj == null)
            return false;
        if(getClass() != obj.getClass())
            return false;
        LabyrinthNode o = (LabyrinthNode)obj;
        if(x != o.x)
            return false;
        if(y != o.y)
            return false;
        return true;
    }

    @Override
    protected Object clone() {
        return new LabyrinthNode(x,y);
    }

    @Override
    public String toString() {
        return "Node ["+x+", "+y+"]";
    }

    public LabyrinthNode forward(int direction){
        switch ( direction %4){
            case 0:{
                return new LabyrinthNode(x,y + 1);
            }
            case 1:{
                return new LabyrinthNode(x + 1,y);
            }
            case 2:{
                return new LabyrinthNode(x, y - 1);
            }
            case 3:{
                return new LabyrinthNode(x - 1,y);
            }
        }
        return null;
    }

    public LabyrinthNode[] getNeighbors(int direction){
        final int DIRECTIONS = 4;
        LabyrinthNode[] neighbors = new LabyrinthNode[DIRECTIONS];
        for (int i = 0; i < DIRECTIONS; i++) neighbors[i] = forward((direction + i)%DIRECTIONS);

        return neighbors;

    }

    public LabyrinthNode[] getNeighbors(){
        return getNeighbors(0);
    }
}
