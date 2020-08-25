package org.dice_group.graph_search;

import org.dice_group.path.Node;

public interface Distance {
	
	/**
	 * computes a new score for a path with an added edge
	 * @param node
	 * @param newEdge
	 * @return
	 */
	double computeDistance(Node node, double [] newEdge);

}
