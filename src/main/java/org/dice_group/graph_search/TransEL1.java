package org.dice_group.graph_search;

import java.util.Arrays;
import java.util.stream.DoubleStream;

import org.dice_group.path.property.Property;
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
	 * TODO change to || (r_1 + r_2 + ... + r_n) - r_p ||, inner products are no longer needed
	 */
	@Override
	public double computeDistance(Property property, double[] newEdge) {
		// (r_1 + r_2 + ... + r_n)
		if (property.getInnerProduct() == null) {
			property.setInnerProduct(newEdge);
		} else {
			property.setInnerProduct(ArrayUtils.computeVectorSummation(property.getInnerProduct(), newEdge));
		}

		double[] concat = property.getInnerProduct();

		// (r_1 ° r_2 ° ... °r_n) - r_p
		double[] res = ArrayUtils.computeVectorSubtraction(concat, targetEdge);

		// separate the real and imaginary part of the vector
		int offset = (int) Math.floor(res.length / 2);
		double[] realPart = Arrays.copyOfRange(res, 0, offset);
		double[] imPart = Arrays.copyOfRange(res, offset, res.length);

		// || (r_1 ° r_2 ° ... °r_n) - r_p ||
		double[] temp = ArrayUtils.computeAbsoluteValue(realPart, imPart);

		return DoubleStream.of(temp).sum();
	}

	public double[] getTargetEdge() {
		return targetEdge;
	}

	public void setTargetEdge(double[] targetEdge) {
		this.targetEdge = targetEdge;
	}

}
