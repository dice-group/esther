package org.dice_group.graph_search.algorithms;

import java.util.HashSet;
import java.util.Set;

import org.dice_group.graph_search.Distance;
import org.dice_group.graph_search.modes.Matrix;
import org.dice_group.path.Graph;
import org.dice_group.path.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

//		// start with the source node
//		Queue<Node> queue = new PriorityQueue<Node>();
//		queue.add(new Node(sourceID));
//
//		int iterations = 0;
//		while (!queue.isEmpty() && iterations < SearchAlgorithm.MAX_PATHS) {
//			// get first and remove it from queue
//			Node node = queue.poll();
//
//			// goal node reached, does it match the range?
//			if (node.getNodeID() == destID && isRangeAllowing(node, destID, relations.length)) {
//				iterations++;
//				LOGGER.info("Path found -> " + node.toString());
//				paths.add(node);
//				continue;
//			}
//
//			// get next nodes in line
//			queue.addAll(getAllowedNodes(graph, node, edgeID, scorer, relations));
//
//		}
		return paths;
	}
}
