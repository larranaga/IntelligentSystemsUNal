package unalcol.agents.examples.labyrinth.multeseo.eater.SII_2017I.hackermen;

import java.util.Comparator;
import java.util.HashMap;

/**
 * Created by larra on 11/03/17.
 */
public class NodeComparator implements Comparator<LabyrinthNode> {

    private HashMap<LabyrinthNode, Integer> dist;

    public NodeComparator(HashMap<LabyrinthNode, Integer> _dist){
        super();
        dist = _dist;
    }

    @Override
    public int compare(LabyrinthNode o1, LabyrinthNode o2) {
        return dist.get(o1) - dist.get(o2);
    }
}
