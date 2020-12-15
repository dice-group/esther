package org.dice_group.models;

import org.apache.commons.math3.complex.Quaternion;
import org.dice_group.path.property.Property;
import org.dice_group.util.ArrayUtils;

public class DensE extends QuatEmbeddingModel {

	public DensE(double[][] relW, double[][] relX, double[][] relY, double[][] relZ) {
		super(relW, relX, relY, relZ);
	}

	@Override
	public double computeDistance(Property property, int index, boolean isNewInverse) {
		Quaternion[] newQuatEdge = ArrayUtils.getQuaternion(relW[index], relX[index], relY[index], relZ[index]);

		double[] invNewNorm = ArrayUtils.getQuatNorm(ArrayUtils.getInverseQuat(newQuatEdge));
		double[] newNorm = ArrayUtils.getQuatNorm(newQuatEdge);

		double[] norm;
		if (property.getInnerProduct() == null && property.getBackPointer() == null) {
			norm = isNewInverse ? invNewNorm : newNorm;
		} else {
			double[] inner = property.getInnerProduct() == null
					? property.getBackPointer().getProperty().getInnerProduct()
					: property.getInnerProduct();
			if (isNewInverse) {
				norm = ArrayUtils.computeHadamardProduct(invNewNorm, inner);
			} else {
				norm = ArrayUtils.computeHadamardProduct(newNorm, inner);
			}
		}

		property.setInnerProduct(norm);
		double[] inner = property.getInnerProduct();
		double[] res = ArrayUtils.computeHadamardProduct(inner, ArrayUtils.getQuatNorm(ArrayUtils.getInverseQuat(targetEdge)));
		double score = ArrayUtils.sumArrayElements(res);

		property.updateCost(score);
		return score;
	}

	public double[][] getRelW() {
		return relW;
	}

	public void setRelW(double[][] relW) {
		this.relW = relW;
	}

	public double[][] getRelX() {
		return relX;
	}

	public void setRelX(double[][] relX) {
		this.relX = relX;
	}

	public double[][] getRelY() {
		return relY;
	}

	public void setRelY(double[][] relY) {
		this.relY = relY;
	}

	public double[][] getRelZ() {
		return relZ;
	}

	public void setRelZ(double[][] relZ) {
		this.relZ = relZ;
	}

	public Quaternion[] getTargetEdge() {
		return targetEdge;
	}

	public void setTargetEdge(Quaternion[] targetEdge) {
		this.targetEdge = targetEdge;
	}

	@Override
	public String toString() {
		return "DensE";
	}
}
