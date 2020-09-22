package org.dice_group.graph_search.algorithms;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

import org.dice_group.graph_search.Distance;
import org.dice_group.graph_search.modes.Matrix;
import org.dice_group.path.BackPointer;
import org.dice_group.path.Graph;
import org.dice_group.path.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import grph.Grph;
import toools.collections.primitive.LucIntSet;

public class AStarSearch implements SearchAlgorithm {

	private static final Logger LOGGER = LoggerFactory.getLogger(AStarSearch.class);

	private Matrix matrix;

	public AStarSearch(Matrix matrix) {
		this.matrix = matrix;
	}

	@Override
	public Set<Node> findPaths(Graph graph, int sourceID, int edgeID, int destID, double[][] relations,
			Distance scorer) {
		Set<Node> paths = new HashSet<Node>();

		// start with the source node
		Queue<Node> queue = new PriorityQueue<Node>();
		queue.add(new Node(sourceID));

		int iterations = 0;
		while (!queue.isEmpty() && iterations < SearchAlgorithm.MAX_PATHS) {
			// get first and remove it from queue
			Node node = queue.poll();

			// goal node reached, does it match the range?
			if (node.getNodeID() == destID && isRangeAllowing(node, destID, relations.length)) {
				iterations++;
				LOGGER.info("Path found -> " + node.toString());
				paths.add(node);
				continue;
			}

			// get next nodes in line
			queue.addAll(getNextNodes(node, graph.getGrph(), scorer, relations, edgeID));

		}
		return paths;
	}

	/**
	 * 
	 * @param node   current node
	 * @param pID    id of the given edge P
	 * @param offset represents the offset in matrix between the property and its
	 *               inverse
	 * @return true if the range of the given predicate matches the range of the
	 *         last property of the path
	 */
	private boolean isRangeAllowing(Node node, int pID, int offset) {
		int edgeID = node.getLastEdge();
		BitSet[] mat = matrix.getEdgeAdjMatrix();
		if (mat[pID + offset].equals(mat[edgeID]) || mat[pID + offset].equals(mat[edgeID + offset])) {
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @param node      current node
	 * @param grph
	 * @param scorer
	 * @param relations relations embeddings
	 * @param pID       id of the given edge P
	 * @return a list of nodes filtered by the matrix
	 */
	public List<Node> getNextNodes(Node node, Grph grph, Distance scorer, double[][] relations, int pID) {
		int offset = relations.length;

		// keep only the allowed edges
		BitSet[] mat = matrix.getEdgeAdjMatrix();
		BackPointer back = node.getFrom();

		// compare r(x_i) with d(x_i+1)
		if (back != null) {
			BitSet range = mat[node.getFrom().getEdge() + offset];
			return getAllowedNodes(range, grph, node, mat, scorer, relations);
		}
		//TODO remove or penalize back tracking
		
		// no backpointer, means it's coming from the source node, so compare
		// d(p) and d(x_i)
		else {
			BitSet domainP = mat[pID];
			return getAllowedNodes(domainP, grph, node, mat, scorer, relations);
		}
	}

	/**
	 * 
	 * @param compareWith bitset that we want to compare the candidates with
	 * @param grph
	 * @param node        current node
	 * @param mat         edge adjacency matrix of domain and range
	 * @param scorer
	 * @param relations
	 * @return list of allowed nodes dictated by the matrix
	 */
	private List<Node> getAllowedNodes(BitSet compareWith, Grph grph, Node node, BitSet[] mat, Distance scorer,
			double[][] relations) {
		List<Node> succNodes = new ArrayList<Node>();
		int offset = relations.length;

		// check the outgoing edges
		LucIntSet outEdges = grph.getOutOnlyEdges(node.getNodeID());
		for (int i : outEdges) {
			BitSet domain = mat[i];
			if (compareWith.equals(domain)) {
				int j = grph.getDirectedSimpleEdgeHead(i);
				Node temp = new Node(new BackPointer(node, i), j, node.getPathLength() + 1);
				temp.setScore(scorer.computeDistance(temp, relations[i])+temp.getPathLength());
				succNodes.add(temp);
			}
		}

		// check the incoming edges (offset applies, looks at tail of directed edge
		// instead)
		LucIntSet inEdges = grph.getInOnlyEdges(node.getNodeID());
		for (int i : inEdges) {
			BitSet domain = mat[i + offset];
			if (compareWith.equals(domain)) {
				int j = grph.getDirectedSimpleEdgeTail(i);
				Node temp = new Node(new BackPointer(node, i), j, node.getPathLength() + 1);
				temp.setScore(scorer.computeDistance(temp, relations[i]));
				succNodes.add(temp);
			}
		}
		return succNodes;
	}
}
