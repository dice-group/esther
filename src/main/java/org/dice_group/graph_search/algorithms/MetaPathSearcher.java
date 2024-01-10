package org.dice_group.graph_search.algorithms;

import java.util.BitSet;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.dice_group.graph_search.modes.Matrix;
import org.dice_group.models.EmbeddingModel;
import org.dice_group.path.property.Property;

/**
 * Implementation for start, anchor and end nodes retrieval through the set bits
 * indices.
 * 
 * @author Alexandra Silva
 *
 */
public class MetaPathSearcher extends PropertySearch {

	/**
	 * Constructor.
	 * 
	 * @param matrix Adjacency matrix
	 * @param eModel Embedding model
	 */
	public MetaPathSearcher(Matrix matrix, EmbeddingModel eModel) {
		super(matrix, eModel);
	}

	@Override
	public Set<Integer> getIntermediateNodes(BitSet[] mat, boolean isLoopAllowed, Property curProperty,
			int maxPathLength, Set<Integer> stopEdges) {
		Set<Integer> intermediateEdges = new HashSet<>();
		int offset = mat.length / 2;
		BitSet setBits = mat[matrix.getInverseID(curProperty.getEdge())];

		/**
		 * intermediate properties get all the properties that have as domain, the range
		 * of the previous property
		 */
		for (int i = 0; i < mat.length; i++) {
			if (setBits.equals(mat[i])) {

				// paths instead of walks
				if (!isLoopAllowed && curProperty.hasAncestor(i, offset)) {
					continue;
				}

				// don't add to queue if path length is exceeded
				if (curProperty.getPathLength() > maxPathLength - 1) {
					continue;
				}

				// don't add to queue if pathLength-1 and not in stop options
				if (curProperty.getPathLength() == maxPathLength - 1 && !stopEdges.contains(matrix.getInverseID(i))) {
					continue;
				}

				// add remainder to queue
				intermediateEdges.add(i);
			}
		}

		return intermediateEdges;
	}

	/**
	 * d_n(P)= 1
	 */
	@Override
	public Set<Integer> getStartingEdges(BitSet[] mat, int targetID) {
		Set<Integer> start = mat[targetID].stream().filter(i -> i != matrix.getInverseID(targetID)).map(i -> {
			return matrix.getInverseID(i);
		}).boxed().collect(Collectors.toSet());
		return start;
	}

	/**
	 * d(P⁻) = 1
	 */
	@Override
	public Set<Integer> getEndingEdges(BitSet[] mat, int targetID) {
		Set<Integer> stopEdges = new HashSet<>();
		int pRange = matrix.getInverseID(targetID);
		for (int i = 0; i < mat.length; i++) {
			int curRange = matrix.getInverseID(i);
			if (mat[pRange].equals(mat[curRange])) {
				stopEdges.add(i);
			}
		}
		return stopEdges;
	}
}
