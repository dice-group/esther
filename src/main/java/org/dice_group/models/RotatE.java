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
			tempInner = isNewInverse ? ArrayUtils.getConjugate(newEdge) : newEdge;
		} else {
			double[] inner = property.getInnerProduct() == null
					? property.getBackPointer().getProperty().getInnerProduct()
					: property.getInnerProduct();
			double[] tempNewEdge = isNewInverse ? ArrayUtils.getConjugate(newEdge) : newEdge;
			tempInner = ArrayUtils.computeHadamardProduct(inner, tempNewEdge);
		}
		property.setInnerProduct(tempInner);
		double[] inner = property.getInnerProduct();

		// (r_1 ° r_2 ° ... °r_n) - r_p
		double[] diff = ArrayUtils.computeVectorSubtraction(inner, targetEdge);

		// sqrt(real² + im²)
		double[] absValues = ArrayUtils.computeComplexAbsoluteValue(diff);

		// sum(abs. values)
		return ArrayUtils.computeVectorsL1(absValues);
	}
}
