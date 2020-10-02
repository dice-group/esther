package org.dice_group.path;

public class NodeBackPointer {
	private Node node;
	private int edge;
	
	public NodeBackPointer() {
		// TODO Auto-generated constructor stub
	}
	
	public NodeBackPointer(Node node,  int edge) {
		this.node = node;
		this.edge = edge;
	}

	public Node getNode() {
		return node;
	}

	public void setNode(Node node) {
		this.node = node;
	}

	public int getEdge() {
		return edge;
	}

	public void setEdge(int edge) {
		this.edge = edge;
	}

	
}
