package unalcol.agents.examples.labyrinth.multeseo.eater.ISI2017I.thewise;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Stack;

import unalcol.agents.Action;
import unalcol.agents.AgentProgram;
import unalcol.agents.Percept;
import unalcol.agents.simulate.util.SimpleLanguage;
import unalcol.types.collection.vector.Vector;

public class Agent1 implements AgentProgram {

	private SimpleLanguage language;
	private Vector<String> cmds = new Vector<String>();	
	private HashSet<Node> visited = new HashSet<>();
	private HashSet<Node> finished = new HashSet<>();
	private int orientation;
	private int currentDepth;
	private int maxDepth;
	private Node root = new Node(0, 0);
	private Node nextNode;
	//private HashMap<Node, ArrayList<Node>> AllChildren;
	

	public Agent1() {
	}

	public Agent1(SimpleLanguage language) {
		this.language = language;		
		orientation = 0;
		currentDepth = 0;
		maxDepth = 22;
		nextNode = root;
		//AllChildren = new HashMap<>();

	}

	public void setLanguage(SimpleLanguage _language) {
		language = _language;
	}

	@Override
	public Action compute(Percept p) {

		if (cmds.size() == 0) {
			Node current = nextNode;		
			visited.add(current);

			boolean PF = ((Boolean) p.getAttribute(language.getPercept(0))).booleanValue();
			boolean PD = ((Boolean) p.getAttribute(language.getPercept(1))).booleanValue();
			boolean PA = ((Boolean) p.getAttribute(language.getPercept(2))).booleanValue();
			boolean PI = ((Boolean) p.getAttribute(language.getPercept(3))).booleanValue();
			boolean MT = ((Boolean) p.getAttribute(language.getPercept(4))).booleanValue();
			boolean FAIL = ((Boolean) p.getAttribute(language.getPercept(5))).booleanValue();

			ArrayList<Node> children = getChildren(current, PF, PD, PA, PI);
			children.remove(current.getParent());
			/*AllChildren.put(current, children);
			for (int i = 0; i < children.size(); i ++)
			{
				if (AllChildren.containsKey(children.get(i)))
				{
					ArrayList<Node> c = AllChildren.get(children.get(i));
					boolean f = true;
					for (int j = 0; j < c.size(); j++)					
						if (!visited.contains(c.get(j))){
							f = false;
							break;
						}
					if (f)
					{
						finished.add(children.get(i));
					}																	
				}
			}
			children = getChildren(current, PF, PD, PA, PI);
			children.remove(current.getParent());
			AllChildren.put(current, children);*/
			int d = -1;

			// Road closed
			if (children.isEmpty())				
				finished.add(current);
			
		
			// Looking for a child
			Node next = null;
			if (!children.isEmpty() && currentDepth <= maxDepth)
				next = getNext(children);
			
			// If exists child
			if (next != null ) {
				nextNode = next;
				currentDepth++;
				d = current.getMove(nextNode, orientation);
			
			// Going back
			} else {
				if (current.equals(root))
				{
					// TODO: Action, Detect cycles					
					visited = new HashSet<>();
					maxDepth += 12;
					nextNode = root;
				}
				else
				{					
					nextNode = current.getParent();
					d = current.getMove(nextNode, orientation);
					currentDepth--;
				}				
			}

			
			if (MT)
				return null;

			// Do action
			if (0 <= d && d < 4) {
				for (int i = 1; i <= d; i++) {
					cmds.add(language.getAction(3)); // rotate
					orientation = (orientation + 1) % 4;
				}
				cmds.add(language.getAction(2)); // advance
			} else
				cmds.add(language.getAction(0)); // die
		}
		String x = cmds.get(0);
		cmds.remove(0);
		return new Action(x);
	}

	@Override
	public void init() {

	}

	private ArrayList<Node> getChildren(Node current, boolean PF, boolean PD, boolean PA, boolean PI) {

		ArrayList<Node> list = new ArrayList<>();

		if (!PF) { // No Wall in front
			int y = 1;
			int x = 0;
			switch (orientation) {
			case 1: // right
				y = 0;
				x = 1;
				break;
			case 2: // back
				y = -1;
				x = 0;
				break;
			case 3: // left
				x = -1;
				y = 0;
				break;
			default:
				y = 1;
				x = 0;

			}
			Node node = new Node(current.getX() + x, current.getY() + y, current);
			if (!finished.contains(node))
				list.add(node);
		}
		if (!PD) { // No wall right
			int y = 0;
			int x = 1;
			switch (orientation) {
			case 1: // right
				y = -1;
				x = 0;
				break;
			case 2: // back
				y = 0;
				x = -1;
				break;
			case 3: // left
				x = 0;
				y = 1;
				break;
			default:
				y = 0;
				x = 1;

			}
			Node node = new Node(current.getX() + x, current.getY() + y, current);
			if (!finished.contains(node))
				list.add(node);
		}
		if (!PA) { // no wall back
			int y = -1;
			int x = 0;
			switch (orientation) {
			case 1: // right
				y = 0;
				x = -1;
				break;
			case 2: // back
				y = 1;
				x = 0;
				break;
			case 3: // left
				x = 1;
				y = 0;
				break;
			default:
				y = -1;
				x = 0;

			}
			Node node = new Node(current.getX() + x, current.getY() + y, current);
			if (!finished.contains(node))
				list.add(node);
		}
		if (!PI) { // no wall left
			int y = 0;
			int x = -1;
			switch (orientation) {
			case 1: // right
				y = 1;
				x = 0;
				break;
			case 2: // back
				y = 0;
				x = 1;
				break;
			case 3: // left
				x = 0;
				y = -1;
				break;
			default:
				y = 0;
				x = -1;

			}
			Node node = new Node(current.getX() + x, current.getY() + y, current);
			if (!finished.contains(node))
				list.add(node);
		}

		return list;
	}

	public Node getNext(ArrayList<Node> children)
	{		
		for (int i = 0; i < children.size(); i++)
			if (!visited.contains(children.get(i))) {
				return children.get(i);
							
			}
		return null;
	}
}
