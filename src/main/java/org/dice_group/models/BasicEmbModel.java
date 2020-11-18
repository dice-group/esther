package org.dice_group.models;

public abstract class BasicEmbModel extends EmbeddingModel {

	protected double[][] relations;
	protected double[] targetEdge;

	public BasicEmbModel(double[][] relations) {
		this.relations = relations;
	}

	public double[][] getRelations() {
		return relations;
	}

	public void setRelations(double[][] relations) {
		this.relations = relations;
	}

	public double[] getTargetEdge() {
		return targetEdge;
	}

	public void setTargetEdge(double[] targetEdge) {
		this.targetEdge = targetEdge;
	}

	@Override
	public void updateTargetEdge(int i) {
		targetEdge = relations[i];
	}

}
