package org.dice_group.models;

import org.dice_group.graph_search.Distance;

public class EmbeddingModel {
	
	protected Distance scorer;
	protected double[][] entities;
	protected double[][] relations;
	
	public EmbeddingModel(double[][] entities, double[][] relations) {
		this.entities = entities;
		this.relations = relations;
	}

	public Distance getScorer() {
		return scorer;
	}

	public void setScorer(Distance scorer) {
		this.scorer = scorer;
	}

	public double[][] getEntities() {
		return entities;
	}

	public void setEntities(double[][] entities) {
		this.entities = entities;
	}

	public double[][] getRelations() {
		return relations;
	}

	public void setRelations(double[][] relations) {
		this.relations = relations;
	}

	
}
