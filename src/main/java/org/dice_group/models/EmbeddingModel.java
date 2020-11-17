package org.dice_group.models;

import org.dice_group.graph_search.distance.Distance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmbeddingModel {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(EmbeddingModel.class);
	
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
	
	/**
	 * 
	 * @param i
	 */
	public void updateScorer(int i) {
		LOGGER.error("Didn't override");
	}
}
