package org.dice_group.graph_search.algorithms;

import java.util.PriorityQueue;
import java.util.Queue;

import org.dice_group.path.Graph;
import org.dice_group.path.Node;
import org.dice_group.path.exceptions.NoPathFoundException;

public class AStarSearch implements SearchAlgorithm{
	
	@Override
	public Node findOtherPaths(Graph graph, int sourceID, int edgeID, int destID, double[][] entitites, double[][] relations) throws NoPathFoundException{
		Queue<Node> queue = new PriorityQueue<Node>();
		queue.add(new Node(sourceID));
		
		while(!queue.isEmpty()) {
			// get first and remove it from queue
			Node node = queue.poll();
			
			// goal node reached, return node
			if(node.getNodeID() == destID) {
				return node;
			}
			
			// get in and outgoing edges (we want to look for paths in both directions)
			queue.addAll(graph.getUndirectedSuccessors(node));
		
		}
		throw new NoPathFoundException("Could not reach end node "+ destID);
	}

}
