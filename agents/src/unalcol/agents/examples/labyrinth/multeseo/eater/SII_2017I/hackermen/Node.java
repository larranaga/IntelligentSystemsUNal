package unalcol.agents.examples.labyrinth.multeseo.eater.SII_2017I.hackermen;

/**
 * Created by larra on 11/03/17.
 */
public class Node {

    private int x, y;

    public Node(){
        x = y = 0;
    }

    public Node(int _x, int _y){
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
        Node o = (Node)obj;
        if(x != o.x)
            return false;
        if(y != o.y)
            return false;
        return true;
    }

    @Override
    protected Object clone() {
        return new Node(x,y);
    }

    @Override
    public String toString() {
        return "Node ["+x+", "+y+"]";
    }

    public Node forward(int direction){
        switch ( direction %4){
            case 0:{
                return new Node(x,y + 1);
            }
            case 1:{
                return new Node(x + 1,y);
            }
            case 2:{
                return new Node(x, y - 1);
            }
            case 3:{
                return new Node(x - 1,y);
            }
        }
        return null;
    }

    public Node[] getNeighbors(int direction){
        final int DIRECTIONS = 4;
        Node[] neighbors = new Node[DIRECTIONS];
        for (int i = 0; i < DIRECTIONS; i++) neighbors[i] = forward((direction + i)%DIRECTIONS);

        return neighbors;

    }

    public Node[] getNeighbors(){
        return getNeighbors(0);
    }
}
