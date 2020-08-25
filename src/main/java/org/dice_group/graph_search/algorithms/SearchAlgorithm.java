package org.dice_group.graph_search.algorithms;

import org.dice_group.path.Graph;
import org.dice_group.path.Node;

public interface SearchAlgorithm {
	// TODO for now it's a constant, should be given value
	int MAX_PATHS = 10;

	Node findOtherPaths(Graph grph, int sourceID, int edgeID, int destID, double[][] entitites, double[][] relations);
}
