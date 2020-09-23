package org.dice_group.path.property;

import java.util.BitSet;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

import org.dice_group.graph_search.Distance;
import org.dice_group.graph_search.algorithms.SearchAlgorithm;
import org.dice_group.graph_search.modes.Matrix;
import org.dice_group.path.Graph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertySearch implements SearchAlgorithm {

	private static final Logger LOGGER = LoggerFactory.getLogger(PropertySearch.class);

	private Matrix matrix;

	// prevent infinite loop
	private final int MAX_ITERATIONS = 50;

	public PropertySearch(Matrix matrix) {
		this.matrix = matrix;
	}

	@Override
	public Set<Property> findPaths(Graph graph, int sourceID, int edgeID, int destID, double[][] relations,
			Distance scorer) {
		BitSet[] mat = matrix.getEdgeAdjMatrix();
		Set<Property> propertyPaths = new HashSet<Property>();

		/**
		 * d(P) = d(x_i) -> starting condition get all properties that share the same
		 * domain
		 */
		Queue<Property> queue = new PriorityQueue<Property>();
		for (int i = 0; i < mat.length; i++) {
			boolean isInverse = i >= mat.length / 2;
			if (mat[edgeID].equals(mat[i])) {
				queue.add(new Property(i, isInverse));
			}
		}

		int iterations = 0;
		while (!queue.isEmpty() && iterations < MAX_ITERATIONS) {
			Property curProperty = queue.poll();

			/**
			 * r(P) = r(j) -> stop condition
			 */
			int curRange = matrix.getInverseID(curProperty.getEdge());
			int pRange = matrix.getInverseID(edgeID);
			if (mat[pRange].equals(mat[curRange])) {
				propertyPaths.add(curProperty);
				LOGGER.info(propertyPaths.size() + " Property path(s) found!");
				LOGGER.info(curProperty.toString());
				iterations++;
				continue;
			}

			/**
			 * d(x_i-) = d(x_i+1) -> intermediate properties get all the properties that
			 * have as domain, the range of the previous property
			 */
			for (int i = 0; i < mat.length; i++) {
				boolean isInverse = i >= mat.length / 2;

				int previousEdge = matrix.getInverseID(curProperty.getEdge());
				if (previousEdge == i || i == edgeID || i == matrix.getInverseID(edgeID)
						|| i == matrix.getInverseID(previousEdge)) {
					continue;
				}

				if (mat[previousEdge].equals(mat[i])) {
					queue.add(new Property(i, new PropertyBackPointer(curProperty), isInverse));
				}
			}
			
		}
		return propertyPaths;
	}

}
