package org.dice_group.models;

import org.dice_group.util.ArrayUtils;

import com.jme3.math.Quaternion;

/**
 * Quaternion-based embedding model
 *
 */
public abstract class QuatEmbeddingModel extends EmbeddingModel {

	protected float[][] relW;
	protected float[][] relX;
	protected float[][] relY;
	protected float[][] relZ;
	protected Quaternion[] targetEdge;

	public QuatEmbeddingModel(float[][] relW, float[][] relX, float[][] relY, float[][] relZ) {
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

	public float[][] getRelW() {
		return relW;
	}

	public void setRelW(float[][] relW) {
		this.relW = relW;
	}

	public float[][] getRelX() {
		return relX;
	}

	public void setRelX(float[][] relX) {
		this.relX = relX;
	}

	public float[][] getRelY() {
		return relY;
	}

	public void setRelY(float[][] relY) {
		this.relY = relY;
	}

	public float[][] getRelZ() {
		return relZ;
	}

	public void setRelZ(float[][] relZ) {
		this.relZ = relZ;
	}

}
