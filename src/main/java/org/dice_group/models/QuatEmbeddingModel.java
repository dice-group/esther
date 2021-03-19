package org.dice_group.models;

/**
 * Quaternion-based embedding model
 *
 */
public abstract class QuatEmbeddingModel extends EmbeddingModel {

	protected double[][] relW;
	protected double[][] relX;
	protected double[][] relY;
	protected double[][] relZ;

	public QuatEmbeddingModel(double[][] relW, double[][] relX, double[][] relY, double[][] relZ) {
		this.relW = relW;
		this.relX = relX;
		this.relY = relY;
		this.relZ = relZ;
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
