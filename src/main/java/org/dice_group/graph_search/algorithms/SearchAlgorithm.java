package org.dice_group.graph_search.algorithms;

import java.util.Set;

import org.dice_group.path.property.Property;

public interface SearchAlgorithm {

	/**
	 * Finds the meta-paths for a given edge, following certain conditions: start
	 * condition -> d(P) = d(x_i) stop condition -> r(P) = r(j) intermediate ->
	 * d(x_i-) = d(x_i+1)
	 * 
	 * @param edgeID
	 * @param k             max number of meta-paths
	 * @param l             max length of meta-paths
	 * @param isLoopAllowed whether loops are allowed in the meta-paths
	 * @return the meta-paths
	 */
	Set<Property> findPaths(int edgeID, int k, int l, boolean isLoopAllowed);
}
