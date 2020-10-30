package org.dice_group.graph_search.distance;

import org.dice_group.path.property.Property;
import org.dice_group.util.ArrayUtils;

/**
 * Computes the L1-norm d = || (r_1 ° r_2 ° ... °r_n) - r_p || 
 *
 */
public class RotatEL1 implements Distance {
	
	/**
	 * r_p ( the predicate we are trying to approximate - given )
	 */
	private double[] targetEdge;


	public RotatEL1(double[] targetEdge) {
		this.targetEdge = targetEdge;
	}

	@Override
	public double computeDistance(Property property, double[] newEdge, boolean isNewInverse) {
		intermediateCalcs(property, newEdge, isNewInverse);
		double[] inner = property.getInnerProduct();
		
		// (r_1 ° r_2 ° ... °r_n) - r_p
		double[] diff = ArrayUtils.computeVectorSubtraction(inner, targetEdge);
		
		// sqrt(real² + im²)
		double [] absValues = ArrayUtils.computeComplexAbsoluteValue(diff);

		// sum(abs. values)
		return ArrayUtils.computeVectorsL1(absValues);
	}
	
	/**
	 * (r_1 ° r_2 ° ... ° r_n)
	 * @param property
	 * @param newEdge
	 * @return
	 */
	private void intermediateCalcs(Property property, double[] newEdge, boolean isNewInverse) {
		if (property.getInnerProduct() == null) {
			property.setInnerProduct(newEdge);
		} else {
			double [] tempNewEdge = newEdge;
			if(isNewInverse) {
				tempNewEdge = ArrayUtils.getConjugate(newEdge);
			}
			property.setInnerProduct(ArrayUtils.computeHadamardProduct(property.getInnerProduct(), tempNewEdge));
		}
	}

	public double[] getTargetEdge() {
		return targetEdge;
	}

	public void setTargetEdge(double[] targetEdge) {
		this.targetEdge = targetEdge;
	}


}
