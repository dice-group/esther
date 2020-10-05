package org.dice_group.models;

import org.dice_group.graph_search.distance.TransEL1;

public class TransE extends EmbeddingModel {

	public TransE(double[][] entities, double[][] relations) {
		super(entities, relations);
	}

	public TransE(double[][] entities, double[][] relations, int i) {
		super(entities, relations);
		scorer = new TransEL1(relations[i]);
	}

	@Override
	public String toString() {
		return "TransE";
	}
}
