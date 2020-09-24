package org.dice_group.models;

import org.dice_group.graph_search.RotatEL1;

public class DensE extends EmbeddingModel {

	public DensE(double[][] entities, double[][] relations) {
		super(entities, relations);
	}

	public DensE(double[][] entities, double[][] relations, int i) {
		super(entities, relations);
		scorer = new RotatEL1(relations[i]);
	}
}
