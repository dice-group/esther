package org.dice_group.graph_search.distance;

import org.dice_group.path.property.Property;

public interface Distance {

	/**
	 * Computes a new score for a path with an added edge, it tags the score to
	 * the property object
	 * 
	 * @param property     object to add the new edge to
	 * @param newEdge      edge to be added
	 * @param isNewInverse true if the new edge is traversed inversely
	 * @return
	 */
	double computeDistance(Property property, double[] newEdge, boolean isNewInverse);

}
