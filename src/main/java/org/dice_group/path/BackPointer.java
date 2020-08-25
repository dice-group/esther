package org.dice_group.path;

public class BackPointer {
	private Node node;
	private int edge;
	
	public BackPointer() {
		// TODO Auto-generated constructor stub
	}
	
	public BackPointer(Node node,  int edge) {
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
