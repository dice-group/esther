package org.dice_group.models;

import org.dice_group.path.property.Property;
import org.dice_group.util.ArrayUtils;

public class TransE extends BasicEmbModel {

	public TransE(double[][] relations) {
		super(relations);
	}

	public TransE(double[][] relations, int i) {
		super(relations);
		updateTargetEdge(i);
	}

	@Override
	public String toString() {
		return "TransE";
	}

	@Override
	public double computeDistance(Property property, int index, boolean isNewInverse) {
		double[] newEdge = relations[index];
		double[] tempInner;
		// starting condition
		if (property.getInnerProduct() == null && property.getBackPointer() == null) {
			tempInner = isNewInverse ? ArrayUtils.flipSignArray(newEdge) : newEdge;
		} else {
			double[] inner = property.getInnerProduct() == null
					? property.getBackPointer().getProperty().getInnerProduct()
					: property.getInnerProduct();
			tempInner = isNewInverse ? ArrayUtils.computeVectorSubtraction(inner, newEdge)
					: ArrayUtils.computeVectorSummation(inner, newEdge);
		}
		property.setInnerProduct(tempInner);
		double[] inner = property.getInnerProduct();

		// r_1 + r_2 + ... + r_n - r_p
		double[] res = ArrayUtils.computeVectorSubtraction(inner, targetEdge);

		// || r_1 + ... + r_n - r_p ||
		double score = ArrayUtils.computeVectorsL1(res);
		property.updateCost(score);
		return score;
	}
}
