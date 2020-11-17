package org.dice_group.graph_search.algorithms;

import java.util.BitSet;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.PriorityBlockingQueue;

import org.dice_group.graph_search.distance.Distance;
import org.dice_group.graph_search.modes.Matrix;
import org.dice_group.path.property.Property;
import org.dice_group.path.property.PropertyBackPointer;

/**
 * Follows a slightly modified A* search algorithm that returns n-paths
 *
 */
public class PropertySearch implements SearchAlgorithm {

	/**
	 * Domain-range adjacency matrix
	 */
	private Matrix matrix;

	/**
	 * Score function to be used depending on the embeddings model used
	 */
	private Distance scorer;

	public PropertySearch(Matrix matrix, Distance scorer) {
		this.matrix = matrix;
		this.scorer = scorer;
	}

	@Override
	public Set<Property> findPaths(int edgeID, double[][] relations, int k, int maxPathLength, boolean isLoopAllowed) {
		BitSet[] mat = matrix.getEdgeAdjMatrix();
		Set<Property> propertyPaths = new HashSet<Property>();
		int offset = mat.length / 2;
		int pathCount = 0;

		/**
		 * d(P) = d(x_i) -> starting condition get all properties that share the same
		 * domain
		 */
		Queue<Property> queue = new PriorityBlockingQueue<Property>();
		for (int i = 0; i < mat.length; i++) {
			boolean isInverse = i >= offset;
			if (mat[edgeID].equals(mat[i])) {
				if(!isLoopAllowed && edgeID == i)
					continue;
				Property curProp = new Property(i, isInverse);
				int index = isInverse ?  i-offset : i;
				scorer.computeDistance(curProp, relations[index], isInverse);
				queue.add(curProp);
			}
		}

		while (!queue.isEmpty() && pathCount < k) {
			Property curProperty = queue.poll();
			
			if (curProperty.getPathLength() > maxPathLength)
				continue;
			
			/**
			 * r(P) = r(j) -> stop condition
			 */
			int curRange = matrix.getInverseID(curProperty.getEdge());
			int pRange = matrix.getInverseID(edgeID);
			if (mat[pRange].equals(mat[curRange])) {
				propertyPaths.add(curProperty);
				pathCount++;
			}

			/**
			 * d(x_i-) = d(x_i+1) -> intermediate properties get all the properties that
			 * have as domain, the range of the previous property
			 */
			for (int i = 0; i < mat.length; i++) {
				boolean isInverse = i >= offset;

				int previousEdge = matrix.getInverseID(curProperty.getEdge());
				if (mat[previousEdge].equals(mat[i])) {
					if(!isLoopAllowed && curProperty.hasAncestor(i, offset)) {
						continue;						
					}
					Property newProp = new Property(i, new PropertyBackPointer(curProperty), isInverse);
					int index = isInverse ?  i-offset : i;
					scorer.computeDistance(newProp, relations[index], isInverse);
					queue.add(newProp);
				}
			}

		}
		return propertyPaths;
	}
}
