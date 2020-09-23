package org.dice_group.path.property;

import java.util.Arrays;

public class Property implements Comparable<Property> {
	private int edge;
	private PropertyBackPointer backPointer;
	private int pathLength;
	private double score;
	private double[] innerProduct;

	public Property(int edge, boolean isInverse) {
		this.edge = edge;
		backPointer = null;
		if (isInverse) {
			this.pathLength = 2;
		} else {
			this.pathLength = 1;
		}
	}

	public Property(int edge, PropertyBackPointer backPointer, double score ,boolean isInverse) {
		this.edge = edge;
		this.backPointer = backPointer;
		if (isInverse) {
			this.pathLength = backPointer.getProperty().getPathLength() + 2;
		} else {
			this.pathLength = backPointer.getProperty().getPathLength() + 1;
		}
		this.score = score + pathLength;
	}

	public double getScore() {
		return score;
	}

	public void addToScore(double score) {
		this.score = score+this.pathLength;
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

	@Override
	public int compareTo(Property arg0) {
		double f = this.score;
		double otherF = arg0.score;

		if (f > otherF) {
			return 1;
		} else if (f < otherF) {
			return -1;
		} else {
			return 0;
		}
	}

	@Override
	public String toString() {
		String del = " <- ";
		StringBuilder builder = new StringBuilder(
				"Score: " + this.score + del + this.edge);
		if (this.backPointer != null) {
			builder.append(del).append(this.backPointer.getProperty().toString());
		}
		return builder.toString();
	}

}
