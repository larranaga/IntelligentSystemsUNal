package unalcol.agents.examples.labyrinth.multeseo.eater.ISI2017I.thewise;

public class Node {
	
	private Position position;
	private Node parent;
	
	public Node(int x, int y, Node parent)
	{
		this.position = new Position(x, y);		
		this.parent = parent;
	}
	
	public Node(int x, int y)
	{
		this.position = new Position(x, y);				
	}
	
	public Node(){
	}
	
	
	public Position getPosition() {
		return position;
	}
	
	public int getX()
	{
		return position.getX();
	}
	
	public int getY()
	{
		return position.getY();
	}
	public void setPosition(Position position) {
		this.position = position;
	}
	public Node getParent() {
		return parent;
	}
	public void setParent(Node parent) {
		this.parent = parent;
	}
	
	public int getMove (Node node, int orientation)	
	{		
		int x = this.getX() - node.getX();
		int y = this.getY() - node.getY();
		
		if (x == 1){	
			// Go left: 3
			return  (4 + (3) - orientation) % 4;
		}
		if (y == 1){
			// Go back: 2
			return (4 + (2) - orientation) % 4;
		}
		if (y == -1){
			// Go up: 0
			return (4 - orientation) % 4;
		}
		if (x == -1){
			// Go right: 1
			return (4 + (1) - orientation) % 4;
		}
				
		return -1;		
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((position == null) ? 0 : position.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Node other = (Node) obj;
		if (position == null) {
			if (other.position != null)
				return false;
		} else if (!position.equals(other.position))
			return false;
		return true;
	}	

}
