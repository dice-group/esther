package org.dice_group.path.property;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.math3.complex.Quaternion;

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
	 * back tracker in the hierarchy TODO: might not be necessary to encase in
	 * another class anymore
	 */
	private PropertyBackPointer backPointer;

	private int pathLength;

	/**
	 * used to ease the score calculations in the case of r_1*...*r_n - r_p
	 */
	private double[] innerProduct;
	
	private Quaternion[] innerQuatProduct;

	/**
	 * is this edge traversed in the opposite direction
	 */
	private boolean isInverse;

	/**
	 * score = heuristics + path length
	 */
	private double fullCost;

	/**
	 * calculated depending on the embeddings model
	 */
	private double heuristics;

	/**
	 * the npmi score of the path
	 */
	private double pathNpmi;

	public Property(int edge) {
		this.edge = edge;
		backPointer = null;
		this.pathLength = 1;
		this.fullCost = pathLength;
	}

	public Property(int edge, boolean isInverse) {
		this.edge = edge;
		backPointer = null;
		this.pathLength = 1;
		this.fullCost = pathLength;
		this.isInverse = isInverse;
	}

	public Property(int edge, PropertyBackPointer backPointer, double heuristics, boolean isInverse) {
		this.edge = edge;
		this.backPointer = backPointer;
		this.pathLength = backPointer.getProperty().getPathLength() + 1;
		this.heuristics = heuristics;
		this.fullCost = heuristics + pathLength;
		this.isInverse = isInverse;
	}

	public Property(int edge, PropertyBackPointer backPointer, boolean isInverse) {
		this.edge = edge;
		this.backPointer = backPointer;
		this.pathLength = backPointer.getProperty().getPathLength() + 1;
		this.fullCost = pathLength;
		this.isInverse = isInverse;
	}

	/**
	 * Constructor to enable object's deep copy
	 * 
	 * @param property
	 */
	public Property(Property property) {
		this.edge = property.getEdge();
		this.pathLength = property.getPathLength();
		this.heuristics = property.getHeuristics();
		this.fullCost = property.getPathCost();
		this.isInverse = property.isInverse();
		this.pathNpmi = property.getPathNPMI();
		this.backPointer = property.getBackPointer() == null ? null
				: new PropertyBackPointer(property.getBackPointer());
		this.innerProduct = property.getInnerProduct() == null ? null
				: Arrays.copyOf(property.getInnerProduct(), property.getInnerProduct().length);
	}

	/**
	 * Updates the path score
	 * 
	 * @param score
	 */
	public void updateCost(double score) {
		this.fullCost = score + pathLength;
		this.heuristics = score;
	}

	/**
	 * 
	 * @return an ordered list of the property ids that constitute this path
	 *         (undirected)
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
	 * @return an ordered list of the property ids that constitute this path, a
	 *         property traversed inversely assumes a negative value
	 */
	public List<Integer> getIDDPath() {
		List<Integer> list = new ArrayList<Integer>();
		int edge = this.isInverse ? -this.edge : this.edge;
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

	/**
	 * 
	 * @return an ordered list of properties that constitute this path
	 */
	public List<Property> getProperties() {
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

	/**
	 * 
	 * @param edge
	 * @param offset
	 * @return true if the path has already used the given edge before (in either direction)
	 */
	public boolean hasAncestor(int edge, int offset) {
		if (isEdgeConsecutive(edge, offset)) {
			return true;
		}
		PropertyBackPointer temp = this.backPointer;
		if(temp == null)
			return false;
		
		while (temp != null) {
			Property curProp = temp.getProperty();
			if(curProp.isEdgeConsecutive(edge, offset)) {
				return true;
			}
			temp = curProp.getBackPointer();
		}
		return false;
	}
	
	public boolean isEdgeConsecutive(int edge, int offset) {
		if (this.edge == edge || this.edge == getInverseID(edge, offset)) {
			return true;
		}
		return false;
	}

	public int getInverseID(int edge, int offset) {
		return edge >= offset ? edge - offset : edge + offset;
	}

	public double[] getInnerProduct() {
		return innerProduct;
	}

	public void setInnerProduct(double[] innerProduct) {
		this.innerProduct = Arrays.copyOf(innerProduct, innerProduct.length);
	}
	
	public Quaternion[] getInnerQuatProduct() {
		return innerQuatProduct;
	}

	public void setInnerQuatProduct(Quaternion[] innerQuatProduct) {
		this.innerQuatProduct = innerQuatProduct;
	}

	public double getHeuristics() {
		return heuristics;
	}

	public double getPathCost() {
		return fullCost;
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

	public double getPathNPMI() {
		return pathNpmi;
	}

	public void setPathNPMI(double pathNpmi) {
		this.pathNpmi = pathNpmi;
	}

	public boolean isInverse() {
		return isInverse;
	}

	public void setInverse(boolean isInverse) {
		this.isInverse = isInverse;
	}

	@Override
	public int compareTo(Property arg0) {
		if (this.fullCost > arg0.fullCost) {
			return 1;
		} else if (this.fullCost < arg0.fullCost) {
			return -1;
		} else {
			return 0;
		}
	}

	@Override
	public String toString() {
		return this.fullCost + " : " + getIDPath().toString();
	}
}
