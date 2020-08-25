package org.dice_group.graph_search.algorithms;

import java.util.PriorityQueue;
import java.util.Queue;

import org.dice_group.graph_search.ComplexL1;
import org.dice_group.path.Graph;
import org.dice_group.path.Node;

public class AStarSearch implements SearchAlgorithm {

	@Override
	public Node findOtherPaths(Graph graph, int sourceID, int edgeID, int destID, double [][] relations)  {
		Queue<Node> queue = new PriorityQueue<Node>();
		queue.add(new Node(sourceID));
		
		ComplexL1 scorer = new ComplexL1(relations[destID]);

		while (!queue.isEmpty()) {
			// get first and remove it from queue
			Node node = queue.poll();

			// goal node reached, return node
			if (node.getNodeID() == destID) {
				return node;
			}

			// get in and outgoing edges (we want to look for paths in both directions)
			queue.addAll(graph.getUndirectedSuccessors(node, scorer, relations));

			// TODO check if infinite loop is created by selecting the same node from 2
			// different directions, what is an acceptable stopping condition then?
			
			// TODO will the optimal path be among the selected? does this even matter?
			
			

		}
		return null;
	}

}
