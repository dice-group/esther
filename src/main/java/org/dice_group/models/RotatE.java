package org.dice_group.models;

import org.dice_group.path.property.Property;
import org.dice_group.util.ArrayUtils;

public class RotatE extends BasicEmbModel {

	public RotatE(double[][] relations) {
		super(relations);
	}

	public RotatE(double[][] relations, int i) {
		super(relations);
		updateTargetEdge(i);
	}

	@Override
	public double computeDistance(Property property, int index, boolean isNewInverse) {
		double[] newEdge = relations[index];
		double[] tempInner;
		// starting condition
		if (property.getInnerProduct() == null && property.getBackPointer() == null) {
			tempInner = isNewInverse ? ArrayUtils.flipSignArray(newEdge) : newEdge;
		}
		// inner contains the phase relations, thus hadamard(a,b) becomes the same as
		// adding the corresponding phases
		else {
			double[] inner = property.getInnerProduct() == null
					? property.getBackPointer().getProperty().getInnerProduct()
					: property.getInnerProduct();
			tempInner = isNewInverse ? ArrayUtils.computeVectorSubtraction(inner, newEdge)
					: ArrayUtils.computeVectorSummation(inner, newEdge);
		}
		property.setInnerProduct(tempInner);
		double[] inner = property.getInnerProduct();

		// (r_1 * r_2 * ... * r_n) - r_p
		double[] realRes = ArrayUtils.computeVectorSubtraction(ArrayUtils.cos(inner), ArrayUtils.cos(targetEdge));
		double[] imRes = ArrayUtils.computeVectorSubtraction(ArrayUtils.sin(inner), ArrayUtils.sin(targetEdge));

		// sum absolute values 
		double score = ArrayUtils.sumArrayElements(realRes) + ArrayUtils.sumArrayElements(imRes);
		property.updateCost(score);
		return score;
	}
}
