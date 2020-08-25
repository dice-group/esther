package org.dice_group.graph_search.algorithms;

import java.util.Set;

import org.dice_group.path.Graph;
import org.dice_group.path.Node;

public interface SearchAlgorithm {
	// TODO for now it's a constant, should be given value
	int MAX_PATHS = 2;

	Set<Node> findPaths(Graph graph, int sourceID, int edgeID, int destID, double[][] relations);
}
