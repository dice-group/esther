package org.dice_group.models;

import org.dice_group.util.ArrayUtils;

import com.jme3.math.Quaternion;

/**
 * Quaternion-based embedding model
 *
 */
public abstract class QuatEmbeddingModel extends EmbeddingModel {

	protected double[][] relW;
	protected double[][] relX;
	protected double[][] relY;
	protected double[][] relZ;
	protected Quaternion[] targetEdge;

	public QuatEmbeddingModel(double[][] relW, double[][] relX, double[][] relY, double[][] relZ) {
		this.relW = relW;
		this.relX = relX;
		this.relY = relY;
		this.relZ = relZ;
	}

	@Override
	public void updateTargetEdge(int i) {
		targetEdge = ArrayUtils.getQuaternion(relW[i], relX[i], relY[i], relZ[i]);
	}

	public Quaternion[] getTargetEdge() {
		return targetEdge;
	}

	public void setTargetEdge(Quaternion[] targetEdge) {
		this.targetEdge = targetEdge;
	}

	public double [][] getRelW() {
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

}
