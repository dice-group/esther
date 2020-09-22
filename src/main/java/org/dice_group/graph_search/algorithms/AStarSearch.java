package org.dice_group.graph_search.algorithms;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

import org.dice_group.graph_search.ComplexL1;
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
	public Set<Node> findPaths(Graph graph, int sourceID, int edgeID, int destID, double[][] relations) {
		Set<Node> paths = new HashSet<Node>();

		Queue<Node> queue = new PriorityQueue<Node>();
		queue.add(new Node(sourceID));

		int iterations = 0;

		// scores are calculated in comparison to P
		ComplexL1 scorer = new ComplexL1(relations[edgeID]);

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

			// get next in line
			queue.addAll(getNextTargets(node, graph.getGrph(), scorer, relations, edgeID));

		}
		return paths;
	}
	
	private boolean isRangeAllowing(Node node, int destID, int offset) {
		int edgeID = node.getFrom().getEdge();
		BitSet[] mat = matrix.getEdgeAdjMatrix();
		if(mat[destID].equals(mat[edgeID]) || mat[destID].equals(mat[edgeID+offset])) {
			return true;
		}
		return false;
	}

	/**
	 * Get the nodes connected to the edges that are allowed by the matrix
	 * 
	 * @param node
	 * @return
	 */
	public List<Node> getNextTargets(Node node, Grph grph, ComplexL1 scorer, double[][] relations, int pID) {
		List<Node> succNodes = new ArrayList<Node>();
		int offset = relations.length;

		// keep only the allowed edges
		BitSet[] mat = matrix.getEdgeAdjMatrix();
		BackPointer back = node.getFrom();

		// compare r(x_i) with d(x_i+1)
		if (back != null) {
			BitSet range = mat[node.getFrom().getEdge() + offset];
			getAllowedNodes(range, grph, node, mat, scorer, relations);
		}
		// no backpointer, means it's coming from the source node, so compare
		// d(p) and d(x_i)
		else {
			BitSet domainP = mat[pID];
			getAllowedNodes(domainP, grph, node, mat, scorer, relations);
		}
		return succNodes;
	}

	/**
	 * 
	 * @param compareWith bitset that we want to compare the candidates with
	 * @param grph
	 * @param node current node
	 * @param mat edge adjacency matrix of domain and range
	 * @param scorer
	 * @param relations
	 * @return list of allowed nodes dictated by the matrix
	 */
	private List<Node> getAllowedNodes(BitSet compareWith, Grph grph, Node node, BitSet[] mat, ComplexL1 scorer, double[][] relations) {
		List<Node> succNodes = new ArrayList<Node>();
		int offset = relations.length;
		
		// check the outgoing edges
		LucIntSet outEdges = grph.getOutOnlyEdges(node.getNodeID());
		for (int i : outEdges) {
			BitSet domain = mat[i];
			if (compareWith.equals(domain)) {
				double score = scorer.computeDistance(node, relations[i]);
				for (int j : grph.getDirectedHyperEdgeTail(i)) {
					succNodes.add(new Node(new BackPointer(node, i), j, node.getPathLength() + 1, score));
				}
			}
		}

		// check the incoming edges (offset applies)
		LucIntSet inEdges = grph.getInOnlyEdges(node.getNodeID());
		for (int i : inEdges) {
			BitSet domain = mat[i + offset];
			if (compareWith.equals(domain)) {
				double score = scorer.computeDistance(node, relations[i]);
				for (int j : grph.getDirectedHyperEdgeHead(i)) {
					succNodes.add(new Node(new BackPointer(node, i), j, node.getPathLength() + 1, score));
				}
			}
		}
		return succNodes;
	}
}
