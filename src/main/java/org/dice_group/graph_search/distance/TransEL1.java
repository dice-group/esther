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

	/**
	 * TODO change || (r_1 + r_2 + ... + r_n) - r_p ||, inner products are no longer
	 * needed, check if it's required for compability with the other models
	 */
	@Override
	public double computeDistance(Property property, double[] newEdge, boolean isNewInverse) {
		if (property.getInnerProduct() == null) {
			property.setInnerProduct(newEdge);
		} else {
			if(isNewInverse) {
				property.setInnerProduct(ArrayUtils.computeVectorSubtraction(property.getInnerProduct(), newEdge));
			} else {
				property.setInnerProduct(ArrayUtils.computeVectorSummation(property.getInnerProduct(), newEdge));
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
