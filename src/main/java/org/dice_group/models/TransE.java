package org.dice_group.models;

import org.dice_group.graph_search.RotatEL1;

public class TransE extends EmbeddingModel {

	public TransE(double[][] entities, double[][] relations) {
		super(entities, relations);
	}

	public TransE(double[][] entities, double[][] relations, int i) {
		super(entities, relations);
		scorer = new RotatEL1(relations[i]);
	}

}
