package org.dice_group.graph_search.distance;

import org.dice_group.path.property.Property;
import org.dice_group.path.property.PropertyBackPointer;
import org.dice_group.util.ArrayUtils;

/**
 * || (r_1 + r_2 + ... + r_n) - r_p||
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
	 * needed
	 */
	@Override
	public double computeDistance(Property property, double[] newEdge) {
		PropertyBackPointer previous = property.getBackPointer();
		double[] inner;
		if (previous == null) {
			inner = setIntermediateCalcs(property, newEdge);
		} else {
			inner = setIntermediateCalcs(previous.getProperty(), newEdge);
		}

		// (r_1 + r_2 + ... + r_n) - r_p
		double[] res = ArrayUtils.computeVectorSubtraction(inner, targetEdge);

		// || (r_1 ° r_2 ° ... °r_n) - r_p ||
		return ArrayUtils.computeVectorsAbsoluteValue(res);
	}

	/**
	 * (r_1 + r_2 + ... + r_n), not really needed in this case
	 * 
	 * @param property
	 * @param newEdge
	 * @return
	 */
	private double[] setIntermediateCalcs(Property property, double[] newEdge) {
		if (property.getInnerProduct() == null) {
			property.setInnerProduct(newEdge);
		} else {
			property.setInnerProduct(ArrayUtils.computeVectorSummation(property.getInnerProduct(), newEdge));
		}
		return property.getInnerProduct();
	}

	public double[] getTargetEdge() {
		return targetEdge;
	}

	public void setTargetEdge(double[] targetEdge) {
		this.targetEdge = targetEdge;
	}

}
