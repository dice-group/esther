package org.dice_group.models;

import org.dice_group.path.property.Property;
import org.dice_group.util.ArrayUtils;

public class TransE extends BasicEmbModel {

	public TransE(double[][] relations) {
		super(relations);
	}

	public TransE(double[][] relations, int i) {
		super(relations);
	}

	@Override
	public String toString() {
		return "TransE";
	}

	@Override
	public double computeDistance(Property property, int index, boolean isNewInverse, int targetID) {
		double[] targetEdge = relations[targetID];
		double[] newEdge = relations[index];
		double[] tempInner;
		// starting condition
		if (property.getInnerProduct() == null && property.getBackPointer() == null) {
			tempInner = isNewInverse ? ArrayUtils.flipSignArray(newEdge) : newEdge;
		} else {
			double[] inner = property.getInnerProduct() == null
					? property.getBackPointer().getInnerProduct()
					: property.getInnerProduct();
			tempInner = isNewInverse ? ArrayUtils.computeVectorSubtraction(inner, newEdge)
					: ArrayUtils.computeVectorSummation(inner, newEdge);
		}
		property.setInnerProduct(tempInner); // deep copy
		double[] inner = property.getInnerProduct();

		// r_1 + r_2 + ... + r_n - r_p
		double[] res = ArrayUtils.computeVectorSubtraction(inner, targetEdge);

		// || r_1 + ... + r_n - r_p ||
		double score = ArrayUtils.sumArrayElements(res);
		property.updateCost(score);
		return score;
	}
}
