package org.dice_group.graph_search.algorithms;

import java.util.Set;

import org.dice_group.path.property.Property;

public interface SearchAlgorithm {
	// TODO for now it's a constant, should be given value
	int MAX_PATH_LENGTH = 3;

	Set<Property> findPaths(int edgeID, double[][] relations, int k);
}
