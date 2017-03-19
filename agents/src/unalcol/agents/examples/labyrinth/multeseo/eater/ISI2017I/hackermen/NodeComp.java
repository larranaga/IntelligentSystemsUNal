package unalcol.agents.examples.labyrinth.multeseo.eater.ISI2017I.hackermen;

import java.util.Comparator;
import java.util.HashMap;

/**
 * Created by larra on 11/03/17.
 */
public class NodeComp implements Comparator<Node> {

    private HashMap<Node, Integer> dist;

    public NodeComp(HashMap<Node, Integer> _dist){
        super();
        dist = _dist;
    }

    @Override
    public int compare(Node o1, Node o2) {
        return dist.get(o1) - dist.get(o2);
    }
}
