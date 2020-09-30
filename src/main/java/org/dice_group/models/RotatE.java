package org.dice_group.models;

import org.dice_group.graph_search.RotatEL1;

public class RotatE extends EmbeddingModel {

	public RotatE(double[][] entities, double[][] relations) {
		super(entities, relations);
	}

	public RotatE(double[][] entities, double[][] relations, int i) {
		super(entities, relations);
		scorer = new RotatEL1(relations[i]);
	}
	
	@Override
	public String toString() {
		return "RotatE";
	}
}
