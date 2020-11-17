package org.dice_group.graph_search.distance;

import org.dice_group.path.property.Property;
import org.dice_group.util.ArrayUtils;

/**
 * Computes the L1 norm: || (r_1 + r_2 + ... + r_n) - r_p||
 *
 */
public class TransEL1 implements Distance {

	/**
	 * r_p ( the predicate we are trying to approximate - given )
	 */
	private double[] targetEdge;

	public TransEL1(double[] targetEdge) {
		this.targetEdge = targetEdge;
	}

	@Override
	public double computeDistance(Property property, double[] newEdge, boolean isNewInverse) {
		
		// starting condition
		if (property.getInnerProduct() == null && property.getBackPointer() == null) {
			
			if(isNewInverse) {
				property.setInnerProduct(ArrayUtils.flipSignArray(newEdge));
			} else {
				property.setInnerProduct(newEdge);
			}
			
		
		} else {
			double [] inner = property.getInnerProduct() == null ? property.getBackPointer().getProperty().getInnerProduct() : property.getInnerProduct();
			if(property.getInnerProduct() != null) {
				System.out.println();
			}
			if(isNewInverse) {
				property.setInnerProduct(ArrayUtils.computeVectorSubtraction(inner, newEdge));
			} else {
				property.setInnerProduct(ArrayUtils.computeVectorSummation(inner, newEdge));
			}
		}
		
		double[] inner = property.getInnerProduct();

		// r_1 + r_2 + ... + r_n - r_p
		double[] res = ArrayUtils.computeVectorSubtraction(inner, targetEdge);

		// || r_1 + ... + r_n - r_p ||
		double score = ArrayUtils.computeVectorsL1(res);
		property.updateCost(score);
		return score;
	}

	public double[] getTargetEdge() {
		return targetEdge;
	}

	public void setTargetEdge(double[] targetEdge) {
		this.targetEdge = targetEdge;
	}

}
