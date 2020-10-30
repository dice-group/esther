package org.dice_group.graph_search.algorithms;

import java.util.Set;

import org.dice_group.path.property.Property;

public interface SearchAlgorithm {
	
	Set<Property> findPaths(int edgeID, double[][] relations, int k, int l, boolean isLoopAllowed);
}
