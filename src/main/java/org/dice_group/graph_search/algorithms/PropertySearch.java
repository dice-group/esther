package org.dice_group.graph_search.algorithms;

import java.util.BitSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;

import org.dice_group.graph_search.modes.Matrix;
import org.dice_group.models.EmbeddingModel;
import org.dice_group.path.property.Property;

/**
 * Follows a modified A* search algorithm that returns n-paths
 *
 */
public abstract class PropertySearch implements SearchAlgorithm {

	/**
	 * Domain-range adjacency matrix
	 */
	protected Matrix matrix;

	/**
	 * KGE model used
	 */
	private EmbeddingModel eModel;

	public PropertySearch(Matrix matrix, EmbeddingModel eModel) {
		this.matrix = matrix;
		this.eModel = eModel;
	}

	@Override
	public Set<Property> findPaths(int edgeID, int k, int maxPathLength, boolean isLoopAllowed, int targetID) {
		BitSet[] mat = matrix.getEdgeAdjMatrix();
		Set<Property> propertyPaths = ConcurrentHashMap.newKeySet();
		int offset = mat.length / 2;
		int pathCount = 0;

		// get only possible starting points and initialize the queue
		Set<Integer> start = getStartingEdges(mat, targetID);
		Queue<Property> queue = new PriorityBlockingQueue<Property>();
		for (int i : start) {
			boolean isInverse = i >= offset;
			Property curProp = new Property(i, isInverse);
			int index = isInverse ? i - offset : i;
			eModel.computeDistance(curProp, index, isInverse, targetID);
			queue.add(curProp);
		}

		// get only possible stopping points for the target edge
		Set<Integer> stopEdges = getEndingEdges(mat, targetID);

		// iterate intermediate properties
		while (!queue.isEmpty() && pathCount < k) {
			Property curProperty = queue.poll();

			// skip if path is longer than desired
			if (curProperty.getPathLength() > maxPathLength)
				continue;

			// if the current property can be admitted as an end predicate
			if (stopEdges.contains(curProperty.getEdge())) {
				propertyPaths.add(curProperty);
				pathCount++;
			}

			// Add intermediate nodes to half-baked paths
			Set<Integer> intermediateNodes = getIntermediateNodes(mat, isLoopAllowed, curProperty,
					maxPathLength, stopEdges);
			for (int curNodeID : intermediateNodes) {
				boolean isInverse = curNodeID >= offset;
				Property newProp = new Property(curNodeID, curProperty, isInverse);
				int index = isInverse ? curNodeID - offset : curNodeID;
				eModel.computeDistance(newProp, index, isInverse, targetID);
				queue.add(newProp);
			}
		}
		return propertyPaths;
	}

	/**
	 * Implementations should return a {@link Set} with all the possible edges that
	 * may be used as start edges.
	 * 
	 * 
	 * @param mat      The edge-adjacency matrix
	 * @param targetID The target's predicate ID
	 * @return All the predicates that share the same domain as the given edge's.
	 */
	public abstract Set<Integer> getStartingEdges(BitSet[] mat, int targetID);

	/**
	 * Implementations should return a {@link Set} with all the possible edges that
	 * may be used as end edges.
	 * 
	 * 
	 * @param mat      The edge-adjacency matrix
	 * @param targetID The target's predicate ID
	 * @return All the predicates that share the same range as the given edge's.
	 */
	public abstract Set<Integer> getEndingEdges(BitSet[] mat, int targetID);

	/**
	 * Implementations should return a {@link Set} with all the possible
	 * intermediate edges. Intermediate edges are any edges between the start and
	 * end edge in a path.
	 * 
	 * @param mat           Edge-adjacency matrix
	 * @param isLoopAllowed True if cycles are allowed in our paths
	 * @param curProperty   The property we want to find a new connection for
	 * @param stopEdges     The set of possible stop edges, the only possible edges
	 *                      to be admitted as last in a path
	 * @param maxPathLength Maximum path length
	 * @return
	 */
	public abstract Set<Integer> getIntermediateNodes(BitSet[] mat, boolean isLoopAllowed, Property curProperty,
			int maxPathLength, Set<Integer> stopEdges);
}
