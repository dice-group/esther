package org.dice_group.graph_search.algorithms;

import java.util.Set;

public interface SearchAlgorithm {
	// TODO for now it's a constant, should be given value
	int MAX_PATHS = 50;

	Set<?> findPaths(int edgeID, double[][] relations);
}
