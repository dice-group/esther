package org.dice_group.models;

//import org.apache.commons.math3.complex.Quaternion;
import org.dice_group.path.property.Property;
import org.dice_group.util.ArrayUtils;

import com.jme3.math.Quaternion;

public class DensE extends QuatEmbeddingModel {

	public DensE(double[][] relW, double[][] relX, double[][] relY, double[][] relZ) {
		super(relW, relX, relY, relZ);
	}

	@Override
	public double computeDistance(Property property, int index, boolean isNewInverse) {
		Quaternion[] newQuatEdge = ArrayUtils.getQuaternion(relW[index], relX[index], relY[index], relZ[index]);
		Quaternion[] innerQuat;

		// (r_1 * r_2 * ... * r_n) 
		if (property.getInnerQuatProduct() == null && property.getBackPointer() == null) {
			innerQuat = isNewInverse ? ArrayUtils.getInverseQuat(newQuatEdge) : newQuatEdge;
		} else {
			Quaternion[] inner = property.getInnerQuatProduct() == null
					? property.getBackPointer().getProperty().getInnerQuatProduct()
					: property.getInnerQuatProduct();
			if (isNewInverse) {
				innerQuat = ArrayUtils.computeHamiltonProduct(newQuatEdge, inner);
			} else {
				innerQuat = ArrayUtils.computeHamiltonProduct(ArrayUtils.getInverseQuat(newQuatEdge), inner);
			}
		}
		property.setInnerQuatProduct(innerQuat);
		
		// (r_1 * r_2 * ... * r_n) - r_p
		Quaternion[] sub = ArrayUtils.computeQuatSubtraction(innerQuat, targetEdge);
		
		// ||(r_1 * r_2 * ... * r_n) - r_p||
		double[] res = ArrayUtils.getQuatNorm(sub);
		double score = ArrayUtils.computeVectorsL2(res);
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
