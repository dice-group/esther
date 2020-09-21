package org.dice_group.graph_search.algorithms;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
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
import it.unimi.dsi.fastutil.ints.IntSet;

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
		// TODO obtain the embedding if we don't already have it
		ComplexL1 scorer = new ComplexL1(relations[edgeID]);

		while (!queue.isEmpty() && iterations < SearchAlgorithm.MAX_PATHS) {
			// get first and remove it from queue
			Node node = queue.poll();

			// goal node reached
			if (node.getNodeID() == destID) {
				iterations++;
				LOGGER.info("Path found -> " + node.toString());
				paths.add(node);
				continue;
			}

			// get in and outgoing edges (we want to look for paths in both directions)
			matrix.getEdgeAdjMatrix();

			queue.addAll(graph.getUndirectedSuccessors(node, scorer, relations));
			
			// get nodes from allowed edges //TODO 
			BitSet[] mat = matrix.getEdgeAdjMatrix();
			queue.addAll(getNextTargets(node, graph.getGrph()));
			
			
		}
		return paths;
	}

	/**
	 * Get the nodes connected to the edges that are allowed TODO
	 * @param node
	 * @return 
	 */
	public Collection<? extends Node> getNextTargets(Node node, Grph grph) {
		int FromEdgeID = node.getFrom().getEdge();
		
		// all edges from/to this vertex
		IntSet edges = grph.getEdgesIncidentTo(node.getNodeID());
		
		// TODO keep only allowed edges
		
		for (int i : edges) {
			IntSet nodes = grph.getVerticesIncidentToEdge(i);
		}
		
		
		return null; //TODO
	}
}
