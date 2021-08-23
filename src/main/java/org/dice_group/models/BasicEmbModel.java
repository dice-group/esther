package org.dice_group.models;

public abstract class BasicEmbModel extends EmbeddingModel {

	protected double[][] relations;

	public BasicEmbModel(double[][] relations) {
		this.relations = relations;
	}

	public double[][] getRelations() {
		return relations;
	}

	public void setRelations(double[][] relations) {
		this.relations = relations;
	}

}
