package org.dice_group.graph_search.modes;

import java.util.List;

import org.apache.jena.rdf.model.Resource;

/**
 * Refers to the different possible ways of building the matrix for the A*
 * search, the different ways that the domain and range can relate to each other
 *
 */
public interface MatrixInterface {

	/**
	 * Compares two sets
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	default boolean compareSets(List<Resource> a, List<Resource> b) {
		return false;
	}

}
