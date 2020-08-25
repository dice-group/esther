package org.dice_group.path;

/**
 * Node class with a Backpointer and comparison through score and path length
 * (heuristics + cost)
 *
 */
public class Node implements Comparable<Node> {
	/**
	 * where did it come from
	 */
	private BackPointer from;

	/**
	 * current node id
	 */
	private int nodeID;

	/**
	 * path length until the current node
	 */
	private int pathLength;

	/**
	 * current estimated score
	 */
	private double score;

	public Node(int startNode) {
		this(null, startNode, 0, 0);
	}

	public Node(BackPointer from, int curNode, int pathLength, double score) {
		this.from = from;
		this.nodeID = curNode;
		this.pathLength = pathLength;
		this.score = score;
	}

	public BackPointer getFrom() {
		return from;
	}

	public void setFromNode(BackPointer from) {
		this.from = from;
	}

	public int getNodeID() {
		return nodeID;
	}

	public void setNodeID(int nodeID) {
		this.nodeID = nodeID;
	}

	public int getPathLength() {
		return pathLength;
	}

	public void setPathLength(int pathLength) {
		this.pathLength = pathLength;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

	@Override
	public int compareTo(Node arg0) {
		double f = this.score + this.pathLength;
		double otherF = arg0.score + arg0.pathLength;

		if (f > otherF) {
			return 1;
		} else if (f < otherF) {
			return -1;
		} else {
			return 0;
		}
	}

}
