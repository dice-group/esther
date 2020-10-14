package org.dice_group.path.property;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents a path 
 *
 */
public class Property implements Comparable<Property> {

	/**
	 * edge id
	 */
	private int edge;
	
	/**
	 * back tracker in the hierarchy
	 */
	private PropertyBackPointer backPointer;
	
	private int pathLength;
	
	/**
	 * used to ease the score calculations in the case of r_1*...*r_n - r_p
	 */
	private double[] innerProduct;
	
	/**
	 * is this edge traversed in the opposite direction
	 */
	private boolean isInverse;
	
	/**
	 * score = heuristics + path length
	 */
	private double score;
	
	/**
	 * the npmi score of the path 
	 */
	private double finalScore;

	public Property(int edge) {
		this.edge = edge;
		backPointer = null;
		this.pathLength = 1;
	}
	
	public Property(int edge, boolean isInverse) {
		this.edge = edge;
		backPointer = null;
		this.pathLength = 1;
		this.isInverse = isInverse;
	}

	public Property(int edge, PropertyBackPointer backPointer, double score, boolean isInverse) {
		this.edge = edge;
		this.backPointer = backPointer;
		this.pathLength = backPointer.getProperty().getPathLength() + 1;
		this.score = score + pathLength;
		this.isInverse = isInverse;
	}

	public double getScore() {
		return score;
	}

	public void addToScore(double score) {
		this.score = score + this.pathLength;
	}

	public double[] getInnerProduct() {
		return innerProduct;
	}

	public void setInnerProduct(double[] innerProduct) {
		this.innerProduct = Arrays.copyOf(innerProduct, innerProduct.length);
	}

	public int getEdge() {
		return edge;
	}

	public void setEdge(int edge) {
		this.edge = edge;
	}

	public PropertyBackPointer getBackPointer() {
		return backPointer;
	}

	public void setBackPointer(PropertyBackPointer backPointer) {
		this.backPointer = backPointer;
	}

	public int getPathLength() {
		return pathLength;
	}

	public void setPathLength(int pathLength) {
		this.pathLength = pathLength;
	}

	public double getFinalScore() {
		return finalScore;
	}

	public void setFinalScore(double finalScore) {
		this.finalScore = finalScore;
	}

	public boolean isInverse() {
		return isInverse;
	}

	public void setInverse(boolean isInverse) {
		this.isInverse = isInverse;
	}

	/**
	 * 
	 * @return
	 */
	public List<Integer> getIDPath() {
		List<Integer> list = new ArrayList<Integer>();
		list.add(this.edge);
		if (this.backPointer != null) {
			PropertyBackPointer temp = this.backPointer;
			while (temp != null) {
				list.add(0, temp.getProperty().getEdge());
				temp = temp.getProperty().getBackPointer();
			}
		}
		return list;
	}
	
	/**
	 * 
	 * @return
	 */
	public List<Integer> getIDDPath() {
		List<Integer> list = new ArrayList<Integer>();
		int edge = this.isInverse ? -this.edge: this.edge;
		list.add(edge);
		if (this.backPointer != null) {
			PropertyBackPointer temp = this.backPointer;
			while (temp != null) {
				int tEdge = temp.getProperty().isInverse ? -temp.getProperty().getEdge() : temp.getProperty().getEdge();
				list.add(0, tEdge);
				temp = temp.getProperty().getBackPointer();
			}
		}
		return list;
	}
	
	
	public List<Property> getPaths() {
		List<Property> list = new ArrayList<Property>();
		list.add(this);
		if (this.backPointer != null) {
			PropertyBackPointer temp = this.backPointer;
			while (temp != null) {
				list.add(0, temp.getProperty());
				temp = temp.getProperty().getBackPointer();
			}
		}
		return list;
	}
	
	@Override
	public int compareTo(Property arg0) {
		if (this.score > arg0.score) {
			return 1;
		} else if (this.score < arg0.score) {
			return -1;
		} else {
			return 0;
		}
	}
	
	@Override
	public String toString() {
		return this.score+ " : "+ getIDPath().toString();
	}
}
