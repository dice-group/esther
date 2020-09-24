package org.dice_group.graph_search.algorithms;

import java.util.BitSet;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

import org.dice_group.graph_search.Distance;
import org.dice_group.graph_search.modes.Matrix;
import org.dice_group.path.property.Property;
import org.dice_group.path.property.PropertyBackPointer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertySearch implements SearchAlgorithm {

	private static final Logger LOGGER = LoggerFactory.getLogger(PropertySearch.class);

	private Matrix matrix;

	private Distance scorer;

	public PropertySearch(Matrix matrix, Distance scorer) {
		this.matrix = matrix;
		this.scorer = scorer;
	}

	@Override
	public Set<Property> findPaths(int edgeID, double[][] relations) {
		BitSet[] mat = matrix.getEdgeAdjMatrix();
		Set<Property> propertyPaths = new HashSet<Property>();
		int offset = mat.length / 2;

		/**
		 * d(P) = d(x_i) -> starting condition get all properties that share the same
		 * domain
		 */
		Queue<Property> queue = new PriorityQueue<Property>();
		for (int i = 0; i < mat.length; i++) {
			boolean isInverse = i >= offset;
			if (mat[edgeID].equals(mat[i])) {
				Property curProp = new Property(i);
				double score;
				if (isInverse) {
					score = scorer.computeDistance(curProp, relations[i - offset]);
				} else {
					score = scorer.computeDistance(curProp, relations[i]);
				}
				curProp.addToScore(score);
				queue.add(curProp);
			}
		}

		while (!queue.isEmpty()) {
			Property curProperty = queue.poll();

			/**
			 * r(P) = r(j) -> stop condition
			 */
			int curRange = matrix.getInverseID(curProperty.getEdge());
			int pRange = matrix.getInverseID(edgeID);
			if (mat[pRange].equals(mat[curRange])) {
				propertyPaths.add(curProperty);
				LOGGER.info(propertyPaths.size() + " Property path(s) found!");
				LOGGER.info(curProperty.getScore()+" : "+curProperty.toString());
				continue;
			}

			/**
			 * d(x_i-) = d(x_i+1) -> intermediate properties get all the properties that
			 * have as domain, the range of the previous property
			 */
			for (int i = 0; i < mat.length; i++) {
				boolean isInverse = i >= offset;

				// TODO test this
				int previousEdge = matrix.getInverseID(curProperty.getEdge());
				if (previousEdge == i || i == edgeID || i == matrix.getInverseID(edgeID)
						|| i == matrix.getInverseID(previousEdge)) {
					continue;
				}

				if (mat[previousEdge].equals(mat[i])) {
					double score;
					if (isInverse) {
						score = scorer.computeDistance(curProperty, relations[i - offset]);
					} else {
						score = scorer.computeDistance(curProperty, relations[i]);
					}

					// TODO test this !propertyHasAncestor(i, curProperty)
					if (curProperty.getPathLength() < SearchAlgorithm.MAX_PATH_LENGTH)
						queue.add(new Property(i, new PropertyBackPointer(curProperty), score));
				}
			}

		}
		return propertyPaths;
	}

	public boolean propertyHasAncestor(int edge, Property p) {
		PropertyBackPointer temp = new PropertyBackPointer(p);
		while (temp != null) {
			Property curProp = temp.getProperty();

			if (curProp.getEdge() == edge) {
				return true;
			}
			if (curProp.getEdge() == matrix.getInverseID(edge)) {
				return true;
			}

			temp = curProp.getBackPointer();
		}

		return false;
	}

}
